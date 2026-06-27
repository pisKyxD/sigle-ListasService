require('dotenv').config();
const app = require('./app');
const sequelize = require('./config/database');

// Cargar modelos para que Sequelize registre las asociaciones
require('./models/Paciente');
require('./models/ListaEspera');

const PORT = process.env.PORT || 8081;

sequelize.sync({ alter: true })
  .then(() => {
    console.log('[DB] Conectado y sincronizado.');
    app.listen(PORT, () => console.log(`[Server] listas-service corriendo en puerto ${PORT}`));
  })
  .catch((err) => {
    console.error('[DB] Error al conectar:', err.message);
    process.exit(1);
  });
