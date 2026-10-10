# Oursour

Monorepo con el backend y la app móvil.

```
backend/   API REST — Spring Boot 4 (Java 21) + Oracle + Flyway
mobile/    App Android — Kotlin + Jetpack Compose + Retrofit
```

## Backend

Requisitos: JDK 21 y Docker (para Oracle local).

```bash
cd backend
docker compose up -d          # Oracle Free en localhost:1521 (usuario oursour/oursour)
./gradlew bootRun             # API en http://localhost:8080
curl localhost:8080/api/ping
```

La conexión se configura con variables de entorno (valores por defecto en `application.yml`):

| Variable      | Default                                       |
|---------------|-----------------------------------------------|
| `DB_URL`      | `jdbc:oracle:thin:@//localhost:1521/FREEPDB1` |
| `DB_USERNAME` | `oursour`                                     |
| `DB_PASSWORD` | `oursour`                                     |

Las migraciones de base de datos van en `backend/src/main/resources/db/migration` (Flyway, `V2__descripcion.sql`, ...).

## Mobile

Abrir la carpeta `mobile/` en Android Studio y ejecutar en un emulador.
En debug la app apunta a `http://10.0.2.2:8080/` (el localhost del PC visto desde el emulador).
