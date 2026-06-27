const router = require('express').Router();
const ctrl = require('../controllers/listaEsperaController');

router.get('/', ctrl.getAll);
router.get('/paginado', ctrl.getAllPaginado);
router.get('/especialidad/:especialidad', ctrl.getByEspecialidad);
router.get('/paciente/email/:email', ctrl.getByPacienteEmail);
router.get('/paciente/:pacienteId/paginado', ctrl.getByPacienteIdPaginado);
router.get('/paciente/:pacienteId', ctrl.getByPacienteId);
router.get('/:id', ctrl.getById);
router.post('/registrar', ctrl.registrar);
router.put('/:id/estado', ctrl.updateEstado);
router.delete('/:id', ctrl.remove);

module.exports = router;
