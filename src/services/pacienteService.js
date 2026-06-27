const Paciente = require('../models/Paciente');

const getAll = async () => await Paciente.findAll();

const getById = async (id) => {
  const p = await Paciente.findByPk(id);
  if (!p) { const e = new Error('Paciente no encontrado'); e.status = 404; throw e; }
  return p;
};

const getByRut = async (rut) => {
  const p = await Paciente.findOne({ where: { rut } });
  if (!p) { const e = new Error('Paciente no encontrado'); e.status = 404; throw e; }
  return p;
};

const getByEmail = async (email) => {
  const p = await Paciente.findOne({ where: { email } });
  if (!p) { const e = new Error('Paciente no encontrado'); e.status = 404; throw e; }
  return p;
};

const create = async (data) => await Paciente.create(data);

const update = async (id, data) => {
  const p = await getById(id);
  await p.update({
    nombre: data.nombre,
    apellido: data.apellido,
    fechaNacimiento: data.fechaNacimiento,
    email: data.email,
    telefono: data.telefono,
    establecimientoId: data.establecimientoId,
  });
  return p;
};

const remove = async (id) => {
  const p = await getById(id);
  await p.destroy();
};

module.exports = { getAll, getById, getByRut, getByEmail, create, update, remove };
