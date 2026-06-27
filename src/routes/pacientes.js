const router = require('express').Router();
const ctrl = require('../controllers/pacienteController');

router.get('/', ctrl.getAll);
router.get('/rut/:rut', ctrl.getByRut);
router.get('/email/:email', ctrl.getByEmail);
router.get('/:id', ctrl.getById);
router.post('/', ctrl.create);
router.put('/:id', ctrl.update);
router.delete('/:id', ctrl.remove);

module.exports = router;
