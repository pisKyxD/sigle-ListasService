const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');
const Paciente = require('./Paciente');

const ListaEspera = sequelize.define('ListaEspera', {
  id: { type: DataTypes.BIGINT, autoIncrement: true, primaryKey: true },
  especialidad: { type: DataTypes.STRING },
  diagnostico: { type: DataTypes.STRING },
  prioridad: { type: DataTypes.ENUM('ALTA', 'MEDIA', 'BAJA') },
  estado: {
    type: DataTypes.ENUM('ESPERA', 'AGENDADO', 'ATENDIDO', 'CANCELADO'),
    defaultValue: 'ESPERA'
  },
  fechaIngreso: { type: DataTypes.DATE, field: 'fecha_ingreso' },
  perteneceGes: { type: DataTypes.BOOLEAN, field: 'pertenece_ges' },
  pacienteId: { type: DataTypes.BIGINT, field: 'paciente_id' },
}, { tableName: 'listas_espera', timestamps: false });

ListaEspera.belongsTo(Paciente, { foreignKey: 'paciente_id', as: 'paciente' });
Paciente.hasMany(ListaEspera, { foreignKey: 'paciente_id', as: 'listas' });

module.exports = ListaEspera;
