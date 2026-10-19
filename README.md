# Oursour

Monorepo con el backend y la app móvil.

```
backend/   API REST — Spring Boot 4 (Java 21) + Oracle + Flyway
mobile/    App Android — Kotlin + Jetpack Compose + Retrofit
```

## Backend

Microservicios Spring Boot 4 (Java 21) + Spring Cloud. Cada servicio es un proyecto Maven independiente.

| Servicio           | Puerto | Rutas en el gateway                | Schema Oracle |
|--------------------|--------|------------------------------------|---------------|
| `eureka-server`    | 8761   | — (dashboard en http://localhost:8761) | —         |
| `api-gateway`      | 8080   | punto de entrada único             | —             |
| `usuario-service`  | 8081   | `/api/auth/**`, `/api/usuarios/**` | `usuario`     |
| `catalogo-service` | 8082   | `/api/catalogo/**`                 | `catalogo`    |
| `receta-service`   | 8083   | `/api/recetas/**`                  | `receta`      |
| `pedido-service`   | 8084   | `/api/pedidos/**`                  | `pedido`      |

Requisitos: JDK 21 y Docker (para Oracle local).

```bash
cd backend
docker compose up -d                          # Oracle Free en localhost:1521, crea un schema por servicio
(cd eureka-server && ./mvnw spring-boot:run)  # primero Eureka
(cd api-gateway && ./mvnw spring-boot:run)
(cd usuario-service && ./mvnw spring-boot:run) # ídem para catalogo, receta y pedido
```

Cada servicio se configura con variables de entorno (defaults en su `application.properties`):

| Variable      | Default                                       |
|---------------|-----------------------------------------------|
| `SERVER_PORT` | ver tabla                                     |
| `EUREKA_URL`  | `http://localhost:8761/eureka/`               |
| `DB_URL`      | `jdbc:oracle:thin:@//localhost:1521/FREEPDB1` |
| `DB_USERNAME` / `DB_PASSWORD` | nombre del schema del servicio |

Las migraciones van en `<servicio>/src/main/resources/db/migration` (Flyway, `V1__descripcion.sql`, ...).
El script `backend/db/init/01_schemas.sql` solo corre al crear el volumen; si ya existía de antes: `docker compose down -v`.

## Mobile

Abrir la carpeta `mobile/` en Android Studio y ejecutar en un emulador (minSdk 26).

App en Kotlin + Jetpack Compose + Material 3, arquitectura MVVM, Room (base de datos local),
Navigation Compose y lectura de QR con ZXing. En esta etapa **no se conecta al backend**:
el catálogo usa datos ficticios y los datos del usuario se guardan en Room.

- Guía de paquetes, base de datos, QR y guion del video: [`mobile/docs/GUIA_APP.md`](mobile/docs/GUIA_APP.md)
- Códigos QR de prueba: [`mobile/docs/qr/`](mobile/docs/qr/)
- Admin de prueba: `admin@oursour.cl` / `admin123`
