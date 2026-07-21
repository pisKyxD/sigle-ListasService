const { Op } = require('sequelize');
const sequelize = require('../config/database');
const ListaEspera = require('../models/ListaEspera');
const Paciente = require('../models/Paciente');
const pacienteService = require('./pacienteService');

const calcularPrioridad = (diagnostico, perteneceGes) => {
  if (perteneceGes) return 'ALTA';
  const d = diagnostico.toLowerCase();
  if (d.includes('urgente') || d.includes('grave')) return 'ALTA';
  if (d.includes('crónico') || d.includes('cronico')) return 'MEDIA';
  return 'BAJA';
};

const toDTO = (lista) => ({
  id: Number(lista.id),
  pacienteId: lista.pacienteId ? Number(lista.pacienteId) : null,
  paciente: lista.paciente || undefined,
  especialidad: lista.especialidad,
  diagnostico: lista.diagnostico,
  prioridad: lista.prioridad,
  estado: lista.estado,
  fechaIngreso: lista.fechaIngreso,
  perteneceGes: lista.perteneceGes,
});

const getAll = async () => {
  const listas = await ListaEspera.findAll({ include: [{ model: Paciente, as: 'paciente' }] });
  return listas.map(toDTO);
};

const getAllPaginado = async (page = 0, size = 10) => {
  const offset = page * size;
  const { count, rows } = await ListaEspera.findAndCountAll({
    include: [{ model: Paciente, as: 'paciente' }],
    limit: size,
    offset,
    order: [['fecha_ingreso', 'DESC']],
  });
  return { content: rows.map(toDTO), totalElements: count, totalPages: Math.ceil(count / size), currentPage: page };
};

const getById = async (id) => {
  const lista = await ListaEspera.findByPk(id, { include: [{ model: Paciente, as: 'paciente' }] });
  if (!lista) { const e = new Error('Registro en lista no encontrado'); e.status = 404; throw e; }
  return toDTO(lista);
};

const getByPacienteId = async (pacienteId) => {
  const listas = await ListaEspera.findAll({ where: { pacienteId }, include: [{ model: Paciente, as: 'paciente' }] });
  return listas.map(toDTO);
};

const getByPacienteIdPaginado = async (pacienteId, page = 0, size = 10) => {
  const offset = page * size;
  const { count, rows } = await ListaEspera.findAndCountAll({
    where: { pacienteId }, include: [{ model: Paciente, as: 'paciente' }], limit: size, offset,
  });
  return { content: rows.map(toDTO), totalElements: count, totalPages: Math.ceil(count / size), currentPage: page };
};

const getByPacienteEmail = async (email) => {
  try {
    const paciente = await pacienteService.getByEmail(email);
    return getByPacienteId(paciente.id);
  } catch { return []; }
};

const registrarPacienteEnLista = async (pacienteData, especialidad, diagnostico, perteneceGes) => {
  let paciente;
  try { paciente = await pacienteService.getByRut(pacienteData.rut); }
  catch { paciente = await pacienteService.create(pacienteData); }

  const prioridad = calcularPrioridad(diagnostico, perteneceGes);

  const lista = await ListaEspera.create({
    pacienteId: paciente.id, especialidad, diagnostico, prioridad,
    estado: 'ESPERA', fechaIngreso: new Date(), perteneceGes: perteneceGes || false,
  });

  lista.paciente = paciente;
  return toDTO(lista);
};

const obtenerListasPorEspecialidad = async (especialidad) => {
  const listas = await ListaEspera.findAll({
    where: { especialidad, estado: 'ESPERA' },
    include: [{ model: Paciente, as: 'paciente' }],
    order: [['prioridad', 'ASC'], ['fecha_ingreso', 'ASC']],
  });
  return listas.map(toDTO);
};

const updateEstado = async (id, estado, diagnostico) => {
  const lista = await ListaEspera.findByPk(id);
  if (!lista) { const e = new Error('Registro en lista no encontrado'); e.status = 404; throw e; }
  const updates = { estado };
  if (diagnostico && diagnostico.trim()) updates.diagnostico = diagnostico;
  await lista.update(updates);
  return toDTO(lista);
};

const remove = async (id) => {
  const lista = await ListaEspera.findByPk(id);
  if (!lista) { const e = new Error('Registro en lista no encontrado'); e.status = 404; throw e; }
  await lista.destroy();
};

const claimCandidato = async (especialidad, excluirIds = []) => {
  return await sequelize.transaction(async (t) => {
    const candidato = await ListaEspera.findOne({
      where: { especialidad, estado: 'ESPERA', id: { [Op.notIn]: excluirIds.length ? excluirIds : [0] } },
      include: [{ model: Paciente, as: 'paciente' }],
      order: [['prioridad', 'ASC'], ['fecha_ingreso', 'ASC']],
      lock: t.LOCK.UPDATE,
      transaction: t,
    });
    if (!candidato) return null;
    await candidato.update({ estado: 'OFRECIDO' }, { transaction: t });
    return toDTO(candidato);
  });
};

const resolverOferta = async (listaEsperaId, nuevoEstado) => {
  const ESTADOS_VALIDOS = ['ESPERA', 'AGENDADO'];
  if (!ESTADOS_VALIDOS.includes(nuevoEstado)) {
    const e = new Error(`nuevoEstado debe ser: ${ESTADOS_VALIDOS.join(', ')}`); e.status = 400; throw e;
  }
  const lista = await ListaEspera.findByPk(listaEsperaId);
  if (!lista) { const e = new Error('Registro en lista no encontrado'); e.status = 404; throw e; }
  await lista.update({ estado: nuevoEstado });
  return toDTO(lista);
};

module.exports = {
  getAll, getAllPaginado, getById, getByPacienteId, getByPacienteIdPaginado,
  getByPacienteEmail, registrarPacienteEnLista, obtenerListasPorEspecialidad,
  updateEstado, remove, claimCandidato, resolverOferta,
};