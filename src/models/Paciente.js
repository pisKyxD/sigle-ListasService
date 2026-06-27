const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const Paciente = sequelize.define('Paciente', {
  id: { type: DataTypes.BIGINT, autoIncrement: true, primaryKey: true },
  rut: { type: DataTypes.STRING, allowNull: false, unique: true },
  nombre: { type: DataTypes.STRING, allowNull: false },
  apellido: { type: DataTypes.STRING },
  fechaNacimiento: { type: DataTypes.DATEONLY, field: 'fecha_nacimiento' },
  email: { type: DataTypes.STRING },
  telefono: { type: DataTypes.STRING },
  establecimientoId: { type: DataTypes.BIGINT, field: 'establecimiento_id' },
}, { tableName: 'pacientes', timestamps: false });

module.exports = Paciente;
