const request = require('supertest');

// Mocks antes de requerir la app
jest.mock('../src/services/listaEsperaService');
jest.mock('../src/services/pacienteService');

const listaService = require('../src/services/listaEsperaService');
const pacienteService = require('../src/services/pacienteService');
const app = require('../src/app');

const mockPaciente = { id: 1, rut: '12345678-9', nombre: 'Juan', apellido: 'González', email: 'juan@test.cl' };
const mockLista = { id: 1, pacienteId: 1, especialidad: 'Cardiología', diagnostico: 'Dolor pecho urgente', prioridad: 'ALTA', estado: 'ESPERA', perteneceGes: true };
const mockPaginado = { content: [mockLista], totalElements: 1, totalPages: 1, currentPage: 0 };

// ===================== LISTAS =====================
describe('GET /api/listas', () => {
  it('retorna lista de espera con 200', async () => {
    listaService.getAll.mockResolvedValue([mockLista]);
    const res = await request(app).get('/api/listas');
    expect(res.status).toBe(200);
    expect(res.body).toHaveLength(1);
    expect(res.body[0].especialidad).toBe('Cardiología');
  });
});

describe('GET /api/listas/paginado', () => {
  it('retorna resultados paginados', async () => {
    listaService.getAllPaginado.mockResolvedValue(mockPaginado);
    const res = await request(app).get('/api/listas/paginado?page=0&size=10');
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('content');
    expect(res.body).toHaveProperty('totalPages');
    expect(res.body.currentPage).toBe(0);
  });
});

describe('GET /api/listas/:id', () => {
  it('retorna lista por ID', async () => {
    listaService.getById.mockResolvedValue(mockLista);
    const res = await request(app).get('/api/listas/1');
    expect(res.status).toBe(200);
    expect(res.body.id).toBe(1);
  });

  it('retorna 500 si no existe', async () => {
    listaService.getById.mockRejectedValue(new Error('Registro en lista no encontrado'));
    const res = await request(app).get('/api/listas/99');
    expect(res.status).toBe(500);
  });
});

describe('GET /api/listas/paciente/:pacienteId', () => {
  it('retorna listas de un paciente', async () => {
    listaService.getByPacienteId.mockResolvedValue([mockLista]);
    const res = await request(app).get('/api/listas/paciente/1');
    expect(res.status).toBe(200);
    expect(res.body[0].pacienteId).toBe(1);
  });
});

describe('GET /api/listas/paciente/email/:email', () => {
  it('retorna listas por email del paciente', async () => {
    listaService.getByPacienteEmail.mockResolvedValue([mockLista]);
    const res = await request(app).get('/api/listas/paciente/email/juan@test.cl');
    expect(res.status).toBe(200);
    expect(res.body).toHaveLength(1);
  });
});

describe('GET /api/listas/especialidad/:especialidad', () => {
  it('retorna listas por especialidad', async () => {
    listaService.obtenerListasPorEspecialidad.mockResolvedValue([mockLista]);
    const res = await request(app).get('/api/listas/especialidad/Cardiología');
    expect(res.status).toBe(200);
    expect(res.body[0].especialidad).toBe('Cardiología');
  });
});

describe('POST /api/listas/registrar', () => {
  it('registra paciente en lista correctamente', async () => {
    listaService.registrarPacienteEnLista.mockResolvedValue(mockLista);
    const res = await request(app).post('/api/listas/registrar').send({
      paciente: { rut: '12345678-9', nombre: 'Juan' },
      especialidad: 'Cardiología',
      diagnostico: 'Dolor pecho urgente',
      perteneceGes: true,
    });
    expect(res.status).toBe(200);
    expect(res.body.prioridad).toBe('ALTA');
  });

  it('retorna 400 si falta especialidad', async () => {
    const res = await request(app).post('/api/listas/registrar').send({
      paciente: { rut: '12345678-9', nombre: 'Juan' },
      diagnostico: 'Dolor',
    });
    expect(res.status).toBe(400);
  });

  it('retorna 400 si falta rut del paciente', async () => {
    const res = await request(app).post('/api/listas/registrar').send({
      paciente: { nombre: 'Juan' },
      especialidad: 'Cardiología',
      diagnostico: 'Dolor',
    });
    expect(res.status).toBe(400);
  });
});

describe('PUT /api/listas/:id/estado', () => {
  it('actualiza estado correctamente', async () => {
    listaService.updateEstado.mockResolvedValue({ ...mockLista, estado: 'AGENDADO' });
    const res = await request(app).put('/api/listas/1/estado?estado=AGENDADO');
    expect(res.status).toBe(200);
    expect(res.body.estado).toBe('AGENDADO');
  });

  it('retorna 400 si estado es inválido', async () => {
    const res = await request(app).put('/api/listas/1/estado?estado=INVALIDO');
    expect(res.status).toBe(400);
  });
});

describe('DELETE /api/listas/:id', () => {
  it('elimina lista correctamente', async () => {
    listaService.remove.mockResolvedValue();
    const res = await request(app).delete('/api/listas/1');
    expect(res.status).toBe(200);
  });
});

// ===================== PACIENTES =====================
describe('GET /api/listas/pacientes', () => {
  it('retorna lista de pacientes', async () => {
    pacienteService.getAll.mockResolvedValue([mockPaciente]);
    const res = await request(app).get('/api/listas/pacientes');
    expect(res.status).toBe(200);
    expect(res.body[0].rut).toBe('12345678-9');
  });
});

describe('GET /api/listas/pacientes/:id', () => {
  it('retorna paciente por ID', async () => {
    pacienteService.getById.mockResolvedValue(mockPaciente);
    const res = await request(app).get('/api/listas/pacientes/1');
    expect(res.status).toBe(200);
    expect(res.body.nombre).toBe('Juan');
  });
});

describe('GET /api/listas/pacientes/rut/:rut', () => {
  it('retorna paciente por RUT', async () => {
    pacienteService.getByRut.mockResolvedValue(mockPaciente);
    const res = await request(app).get('/api/listas/pacientes/rut/12345678-9');
    expect(res.status).toBe(200);
    expect(res.body.rut).toBe('12345678-9');
  });
});

describe('GET /api/listas/pacientes/email/:email', () => {
  it('retorna paciente por email', async () => {
    pacienteService.getByEmail.mockResolvedValue(mockPaciente);
    const res = await request(app).get('/api/listas/pacientes/email/juan@test.cl');
    expect(res.status).toBe(200);
    expect(res.body.email).toBe('juan@test.cl');
  });

  it('retorna 404 si no existe', async () => {
    pacienteService.getByEmail.mockRejectedValue(new Error('Paciente no encontrado'));
    const res = await request(app).get('/api/listas/pacientes/email/noexiste@test.cl');
    expect(res.status).toBe(404);
  });
});

describe('POST /api/listas/pacientes', () => {
  it('crea paciente correctamente', async () => {
    pacienteService.create.mockResolvedValue(mockPaciente);
    const res = await request(app).post('/api/listas/pacientes').send({
      rut: '12345678-9', nombre: 'Juan', apellido: 'González', email: 'juan@test.cl',
    });
    expect(res.status).toBe(200);
    expect(res.body.id).toBe(1);
  });
});

describe('PUT /api/listas/pacientes/:id', () => {
  it('actualiza paciente correctamente', async () => {
    pacienteService.update.mockResolvedValue({ ...mockPaciente, nombre: 'Juan Actualizado' });
    const res = await request(app).put('/api/listas/pacientes/1').send({ nombre: 'Juan Actualizado' });
    expect(res.status).toBe(200);
    expect(res.body.nombre).toBe('Juan Actualizado');
  });
});

describe('DELETE /api/listas/pacientes/:id', () => {
  it('elimina paciente correctamente', async () => {
    pacienteService.remove.mockResolvedValue();
    const res = await request(app).delete('/api/listas/pacientes/1');
    expect(res.status).toBe(200);
  });
});

describe('GET /actuator/health', () => {
  it('retorna status UP', async () => {
    const res = await request(app).get('/actuator/health');
    expect(res.status).toBe(200);
    expect(res.body.status).toBe('UP');
  });
});
