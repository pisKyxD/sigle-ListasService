# Sigle-ListasService

Microservicio del sistema SIGLE que gestiona las listas de espera GES y los pacientes asociados.

> Migrado desde Spring Boot (Java) a Node.js / Express / Sequelize.

## Stack

- Node.js 20
- Express 5
- express-validator
- Sequelize + MySQL2
- Eureka (registro de servicio)
- Jest + Supertest (testing)
- pnpm (gestor de paquetes)

## Requisitos

- Node.js 20+
- pnpm (`npm install -g pnpm`)
- MySQL corriendo

## Variables de entorno

Copiar `.env.example` a `.env`:

```env
PORT=8081
DB_HOST=localhost
DB_PORT=3306
DB_NAME=sigle_listas
DB_USER=root
DB_PASSWORD=tu_password

EUREKA_HOST=localhost
EUREKA_PORT=8761
INSTANCE_HOST=localhost
```

## Instalación

```bash
pnpm install
pnpm dev
```

Disponible en `http://localhost:8081`

## Docker

```bash
docker build -t sigle-listas-service .
docker run -p 8081:8081 sigle-listas-service
```

## Endpoints

### Listas de espera
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/listas` | Todas las listas |
| GET | `/api/listas/paginado?page=&size=` | Todas, paginadas |
| GET | `/api/listas/:id` | Por ID |
| GET | `/api/listas/paciente/:pacienteId` | Listas de un paciente |
| GET | `/api/listas/paciente/:pacienteId/paginado?page=&size=` | Listas de un paciente, paginadas |
| GET | `/api/listas/paciente/email/:email` | Listas por email del paciente |
| GET | `/api/listas/especialidad/:especialidad` | Por especialidad, ordenadas por prioridad |
| POST | `/api/listas/registrar` | Registrar paciente en lista (crea al paciente si no existe) |
| PUT | `/api/listas/:id/estado` | Actualizar estado |
| DELETE | `/api/listas/:id` | Eliminar |

### Pacientes
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/listas/pacientes` | Todos |
| GET | `/api/listas/pacientes/:id` | Por ID (usado por PacientesService para obtener el email al enviar correos) |
| GET | `/api/listas/pacientes/rut/:rut` | Por RUT |
| GET | `/api/listas/pacientes/email/:email` | Por email |
| POST | `/api/listas/pacientes` | Crear |
| PUT | `/api/listas/pacientes/:id` | Actualizar |
| DELETE | `/api/listas/pacientes/:id` | Eliminar |

## Ejemplo registro en lista

```json
POST /api/listas/registrar
{
  "paciente": {
    "nombre": "Juan",
    "apellido": "González",
    "rut": "12345678-9",
    "email": "juan@ejemplo.cl",
    "fechaNacimiento": "1985-03-15"
  },
  "especialidad": "Cardiología",
  "diagnostico": "Arritmia urgente",
  "perteneceGes": true
}
```

## Prioridad automática

Al registrar, el servicio calcula la prioridad según estas reglas:

- `perteneceGes = true` → **ALTA**
- Diagnóstico contiene "urgente" o "grave" → **ALTA**
- Diagnóstico contiene "crónico" → **MEDIA**
- Cualquier otro caso → **BAJA**

## Estados posibles

`ESPERA` → `AGENDADO` → `ATENDIDO` / `CANCELADO`

## Validaciones

Los endpoints de creación/actualización validan el body con `express-validator`. Peticiones con datos faltantes o inválidos devuelven `400` con el detalle del campo.

## Consumido por otros servicios

- **CitasService** llama a `/api/listas/pacientes/:id` y a este servicio en general al momento de agendar una cita (vía el flujo de derivación).
- **PacientesService** llama a `/api/listas/pacientes/:id` para obtener el email del paciente antes de enviar una notificación por correo.

## Tests

```bash
pnpm test
```

Corre con Jest + Supertest, usando mocks del modelo Sequelize (no requiere BD real).

### Tests con Docker

```bash
docker build -f Dockerfile.test -t listas-tests .
docker run --rm listas-tests
```

## Health
GET http://localhost:8081/actuator/health

## Estructura
src/
├── app.js
├── index.js
├── config/
│   ├── database.js
│   └── eureka.js
├── controllers/
│   ├── listaEsperaController.js
│   └── pacienteController.js
├── models/
│   ├── ListaEspera.js
│   └── Paciente.js
├── routes/
│   ├── listas.js
│   └── pacientes.js
└── services/
├── listaEsperaService.js
└── pacienteService.js