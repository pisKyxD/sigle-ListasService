require('dotenv').config();
const express = require('express');

const app = express();
app.use(express.json());

// Rutas
app.use('/api/listas/pacientes', require('./routes/pacientes'));
app.use('/api/listas', require('./routes/listas'));

// Health
app.get('/actuator/health', (req, res) => res.json({ status: 'UP', service: 'listas-service' }));

// Error handler global
app.use((err, req, res, next) => {
  const status = err.status || 500;
  res.status(status).json({ error: err.message || 'Error interno del servidor' });
});

module.exports = app;
