const service = require('../services/listaEsperaService');

const getAll = async (req, res, next) => {
  try { res.json(await service.getAll()); } catch (e) { next(e); }
};

const getAllPaginado = async (req, res, next) => {
  try {
    const page = parseInt(req.query.page) || 0;
    const size = parseInt(req.query.size) || 10;
    res.json(await service.getAllPaginado(page, size));
  } catch (e) { next(e); }
};

const getById = async (req, res, next) => {
  try { res.json(await service.getById(req.params.id)); } catch (e) { next(e); }
};

const getByPacienteId = async (req, res, next) => {
  try { res.json(await service.getByPacienteId(req.params.pacienteId)); } catch (e) { next(e); }
};

const getByPacienteIdPaginado = async (req, res, next) => {
  try {
    const page = parseInt(req.query.page) || 0;
    const size = parseInt(req.query.size) || 10;
    res.json(await service.getByPacienteIdPaginado(req.params.pacienteId, page, size));
  } catch (e) { next(e); }
};

const getByPacienteEmail = async (req, res, next) => {
  try { res.json(await service.getByPacienteEmail(req.params.email)); } catch (e) { next(e); }
};

const registrar = async (req, res, next) => {
  try {
    const { paciente, especialidad, diagnostico, perteneceGes } = req.body;
    if (!paciente || !especialidad || !diagnostico) {
      return res.status(400).json({ error: 'paciente, especialidad y diagnostico son obligatorios' });
    }
    if (!paciente.rut || !paciente.nombre) {
      return res.status(400).json({ error: 'El paciente debe tener rut y nombre' });
    }
    res.json(await service.registrarPacienteEnLista(paciente, especialidad, diagnostico, perteneceGes));
  } catch (e) { next(e); }
};

const getByEspecialidad = async (req, res, next) => {
  try { res.json(await service.obtenerListasPorEspecialidad(req.params.especialidad)); } catch (e) { next(e); }
};

const updateEstado = async (req, res, next) => {
  try {
    const { estado, diagnostico } = req.query;
    const ESTADOS_VALIDOS = ['ESPERA', 'AGENDADO', 'ATENDIDO', 'CANCELADO'];
    if (!estado || !ESTADOS_VALIDOS.includes(estado)) {
      return res.status(400).json({ error: `Estado debe ser uno de: ${ESTADOS_VALIDOS.join(', ')}` });
    }
    res.json(await service.updateEstado(req.params.id, estado, diagnostico));
  } catch (e) { next(e); }
};

const remove = async (req, res, next) => {
  try { await service.remove(req.params.id); res.json({ message: 'Lista eliminada' }); } catch (e) { next(e); }
};

module.exports = {
  getAll, getAllPaginado, getById, getByPacienteId, getByPacienteIdPaginado,
  getByPacienteEmail, registrar, getByEspecialidad, updateEstado, remove,
};
