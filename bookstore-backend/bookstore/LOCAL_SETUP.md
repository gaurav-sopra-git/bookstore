# Local development setup (without Docker)

This project is intended to run against locally installed services. Docker and Docker Compose are not required.

## Required software

- JDK 21
- MySQL Server 8.4 (or a compatible MySQL 8.x release)
- Maven (optional; the project includes Maven Wrapper scripts)

## Optional infrastructure used by the application

- Redis 7.x: used for book catalogue caching. Configure `REDIS_HOST` and `REDIS_PORT` if your local Redis uses non-default settings.
- Apache Kafka 4.x: used to publish an `OrderPlacedEvent` after an order transaction commits. Configure `KAFKA_BOOTSTRAP_SERVERS` if your local broker uses a different address.

Redis and Kafka are application integrations, not substitutes for MySQL. MySQL remains the source of truth.

## 1. Create the MySQL database

Start your locally installed MySQL server, then run:

```sql
CREATE DATABASE bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

The default connection is:

- URL: `jdbc:mysql://localhost:3306/bookstore`
- Username: `root`
- Password: `root`

Do not use the sample password outside local development. Override credentials with environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

Hibernate is currently configured with `spring.jpa.hibernate.ddl-auto=update` for the kata's local development workflow. For a production deployment, use schema migrations (for example Flyway) and set Hibernate DDL mode to `validate`.

## 2. Start Redis

Install Redis for your operating system and start the Redis server using its normal service command. The application expects `localhost:6379` by default.

Override with `REDIS_HOST` and `REDIS_PORT` when needed.

## 3. Start Kafka

Install and start a local Apache Kafka broker. The application expects the broker at `localhost:9092` by default.

The application creates/configures its order topic through its Kafka topic configuration. If your broker requires authentication or TLS, additional producer security properties will be needed.

Override the broker address with `KAFKA_BOOTSTRAP_SERVERS`.

## 4. Configure the application secrets

Set a strong local JWT secret (at least 32 bytes) using `JWT_SECRET`. You can also configure:

- `JWT_EXPIRATION_MS` (default: 86400000)
- `CORS_ALLOWED_ORIGINS` (default: `http://localhost:5173,http://localhost:3000`)
- `SERVER_PORT` (default: `8080`)

## 5. Build and run

On Windows:

```powershell
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Startup order

1. MySQL
2. Redis
3. Kafka
4. Spring Boot application

For the first local run, verify each service independently before starting the application. If Redis or Kafka is intentionally unavailable, the related cache/event functionality may not work; MySQL-backed catalogue and order persistence remain the core application capabilities.
