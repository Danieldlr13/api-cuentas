# api-cuentas

API REST pequeña con Java y Spring Boot para crear cuentas, consultarlas y transferir saldo entre ellas.

## Qué hace

- `POST /cuentas`: crea una cuenta (`titular`, `saldoInicial`).
- `GET /cuentas`: lista las cuentas.
- `GET /cuentas/{id}`: consulta una cuenta.
- `POST /cuentas/transferencias`: transfiere un monto entre dos cuentas dentro de una transacción.

Errores: cuenta inexistente responde 404; saldo insuficiente o datos no válidos (monto <= 0, titular vacío, etc.) responden 400. El cuerpo del error es `{"error": "..."}`.

## Requisitos

- Java 21
- Docker con Docker Compose (solo para ejecutar la app; las pruebas no lo necesitan)
- No hace falta instalar Maven: se usa `./mvnw`

## Levantar la base de datos y la app

```bash
docker compose up -d     # PostgreSQL 16 en localhost:5432
./mvnw spring-boot:run   # API en http://localhost:8080
```

Las credenciales de `docker-compose.yml` y de `application.properties` son de desarrollo y coinciden entre sí. La tabla se crea sola (`ddl-auto=update`).

Para apagar y borrar la base, incluido su volumen:

```bash
docker compose down -v
```

## Pruebas

```bash
./mvnw test
```

Son 7 pruebas: 4 unitarias de `CuentaService` (JUnit 5 y Mockito) y 3 del controlador con MockMvc. No usan base de datos ni Docker.

## Ejemplos con curl

```bash
# Crear
curl -X POST localhost:8080/cuentas -H 'Content-Type: application/json' \
  -d '{"titular":"Ana","saldoInicial":100}'
curl -X POST localhost:8080/cuentas -H 'Content-Type: application/json' \
  -d '{"titular":"Luis","saldoInicial":50}'

# Listar
curl localhost:8080/cuentas

# Consultar una
curl localhost:8080/cuentas/1

# Transferir 30 de la cuenta 1 a la 2
curl -X POST localhost:8080/cuentas/transferencias -H 'Content-Type: application/json' \
  -d '{"origenId":1,"destinoId":2,"monto":30}'

# Error 404: cuenta inexistente
curl -i localhost:8080/cuentas/99

# Error 400: saldo insuficiente
curl -i -X POST localhost:8080/cuentas/transferencias -H 'Content-Type: application/json' \
  -d '{"origenId":1,"destinoId":2,"monto":500}'
```

## Estructura

```
src/main/java/co/danieldlr/cuentas/
  ApiCuentasApplication.java   punto de entrada
  CuentaController.java        endpoints
  CuentaService.java           lógica y transacción
  CuentaRepository.java        acceso a datos (Spring Data JPA)
  Cuenta.java                  entidad
  *Request.java                DTO de entrada (record) con validaciones
  ManejadorErrores.java        @RestControllerAdvice
  *Exception.java              errores de negocio
```

## Limitaciones

- La transferencia no usa bloqueos: dos transferencias simultáneas sobre la misma cuenta podrían pisarse. No se manejó concurrencia a propósito.
- No hay autenticación, migraciones ni paginación.
