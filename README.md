# Sigle-ListasService

Microservicio del sistema SIGLE que gestiona las listas de espera GES y los pacientes asociados.

## Stack

- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- MySQL
- Lombok

## Requisitos

- Java 17+
- Maven 3.9+
- MySQL corriendo

La base de datos `sigle_listas` se crea automáticamente al iniciar.

## Configuración

```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/sigle_listas?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=tu_password
```

## Instalación

```bash
mvn clean package -DskipTests
java -jar target/listas-service-0.0.1-SNAPSHOT.jar
```

Disponible en `http://localhost:8081`

## Docker

```bash
docker build -t sigle-listas-service .
docker run -p 8081:10000 sigle-listas-service
```

## Endpoints

### Listas de espera
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/listas` | Todas las listas |
| GET | `/api/listas/{id}` | Por ID |
| GET | `/api/listas/paciente/{pacienteId}` | Listas de un paciente |
| GET | `/api/listas/paciente/email/{email}` | Listas por email |
| POST | `/api/listas/registrar` | Registrar paciente en lista |
| GET | `/api/listas/especialidad/{especialidad}` | Por especialidad, ordenadas por prioridad |
| PUT | `/api/listas/{id}/estado` | Actualizar estado |
| DELETE | `/api/listas/{id}` | Eliminar |

### Pacientes
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/listas/pacientes` | Todos |
| GET | `/api/listas/pacientes/{id}` | Por ID |
| GET | `/api/listas/pacientes/rut/{rut}` | Por RUT |
| GET | `/api/listas/pacientes/email/{email}` | Por email |
| POST | `/api/listas/pacientes` | Crear |
| PUT | `/api/listas/pacientes/{id}` | Actualizar |
| DELETE | `/api/listas/pacientes/{id}` | Eliminar |

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

Los endpoints de creación/actualización validan el body con `@Valid` y anotaciones `@NotNull` / `@NotBlank` de Jakarta Validation.

## Patrones

**Repository con derived query:** `findByEspecialidadAndEstadoOrderByPrioridadAscFechaIngresoAsc` genera el SQL automáticamente del nombre del método, ordenando primero por prioridad y luego por fecha de ingreso.

**DTO:** `ListaEsperaDTO` evita problemas de serialización con las relaciones lazy de Hibernate. Usa un factory method estático `ListaEsperaDTO.from(lista)`.

## Tests

```bash
mvn test
```

Incluye tests unitarios para `ListaEsperaService` y `PacienteService`, y tests de integración (`MockMvc`) para `ListaEsperaController` y `PacienteController`, usando H2 en memoria.

### Tests con Docker

```bash
docker build -f Dockerfile.test -t listas-tests .
docker run --rm listas-tests
```

## Health

```
GET http://localhost:8081/actuator/health
```

## Estructura

```
src/main/java/com/rednorte/sigle/listas_service/
├── controller/
├── dto/
├── model/
├── repository/
└── service/
```