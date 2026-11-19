# Bookworm -- Online Bookstore

A small full-stack bookstore application built with Spring Boot and
React. It covers the main journey of browsing books, managing a shopping
bag, creating an account, placing an order and paying through Stripe
Checkout.

The project is split into two applications:

- **Backend:** Java 21, Spring Boot, Spring Data JPA, MySQL, Spring
  Security/JWT, Redis, Kafka and Stripe.
- **Frontend:** React 18, Vite and React Router.

## What you can do

- Browse the book catalogue, search by title or author, and view a
  book's details.
- Add books to a guest or signed-in shopping bag, change quantities
  and remove items.
- Register and sign in.
- Place an order and continue to Stripe Checkout.
- View previous orders after signing in.
- Switch between light and dark appearance.

## Project structure

The suggested repository layout is:

```text
bookworm/
├── README.md
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/bookstore/app/
│       │   │   ├── configs/
│       │   │   ├── controllers/
│       │   │   ├── dtos/
│       │   │   ├── entities/
│       │   │   ├── enums/
│       │   │   ├── events/
│       │   │   ├── exceptions/
│       │   │   ├── mappers/
│       │   │   ├── repositories/
│       │   │   ├── security/
│       │   │   └── services/
│       │   └── resources/
│       └── test/
└── frontend/
    ├── package.json
    ├── package-lock.json
    ├── index.html
    └── src/
        ├── components/
        ├── pages/
        ├── utils/
        ├── api.js
        ├── App.jsx
        ├── main.jsx
        └── styles.css
```

The backend is organised by responsibility: controllers expose the API,
services hold application logic, repositories access the database,
entities represent persisted data, and DTOs define the request/response
shapes. The frontend keeps page components, shared components, API calls
and formatting helpers separate.

## Prerequisites

Install or have access to:

Requirement Version / default

---

Java JDK 21
Maven 3.9.x recommended
Node.js 20 LTS or newer
npm Included with Node.js
MySQL MySQL 8
Redis Local Redis server, default port `6379`
Apache Kafka Local broker, default `localhost:9092`
Stripe Stripe test account and Stripe CLI

The application is configured for local development without Docker.
MySQL, Redis and Kafka can run as separate local processes. Kafka can
also run in WSL2 while the Java application runs on Windows, provided
the broker advertises an address reachable from Windows.

## 1. Clone the repository

```bash
git clone
cd bookworm
```

The commands below assume the folders are named `backend` and
`frontend`.

## 2. Create the MySQL database

Start MySQL and create an empty database:

```sql
CREATE DATABASE bookstore;
```

Create or choose a MySQL user that has permission to create and update
tables in this database.

The backend uses Hibernate `ddl-auto=update` for local development, so
the tables are created/updated when the application starts. This is
convenient locally; use a controlled migration tool such as Flyway or
Liquibase rather than relying on `update` in production.

## 3. Start Redis

Start your local Redis server and make sure it is listening on:

```text
localhost:6379
```

Some useful commands

```
sudo service redis-server start
sudo service redis-server status
sudo service redis-server stop
sudo service redis-server restart
```

Redis is used by Spring's cache abstraction for book lookups and
searches. If you change the host or port, update the backend environment
variables in the next step.

## 4. Start Kafka

Start a local Kafka broker and make sure the backend can reach:

```text
localhost:9092
```

Some useful commands (check your local installation directory and update accordingly)

```
bin/kafka-server-start.sh config/server.properties
```

The application declares the `bookstore.order-placed` topic and
publishes order-related events after database transactions commit. A
single local broker is sufficient for development.

If Kafka runs inside WSL2 and the backend runs on Windows, check the
broker's `advertised.listeners`; advertising `localhost` from inside
WSL2 may not be reachable in every Windows/WSL setup. Use an address
that the Windows host can reach.

## 5. Configure and start the backend

Copy the backend `pom.xml` and `src` directory into `backend/`.

Set the following environment variables before starting the application:

---

Variable Example Required

---

`DB_URL` `jdbc:mysql://localhost:3306/bookstore?useSSL=false&serverTimezone=UTC` Yes

`DB_USER_NAME` `bookstore_user` Yes

`DB_PASSWORD` your local MySQL password Yes

`JWT_SECRET` a random secret of at least 32 bytes Recommended

`JWT_EXPIRATION_MS` `86400000` No; defaults to 24
hours

`CORS_ALLOWED_ORIGINS` `http://localhost:5173` No

`REDIS_HOST` `localhost` No

`REDIS_PORT` `6379` No

`KAFKA_BOOTSTRAP_SERVERS` `localhost:9092` No

`STRIPE_SECRET_KEY` `sk_test_...` Yes for payment flow

`STRIPE_WEBHOOK_SECRET` `whsec_...` Yes for webhook
handling

`APP_FRONTEND_URL` `http://localhost:5173` No

`SERVER_PORT` `8080` No

---

The defaults for Redis, Kafka, CORS, frontend URL and server port are
shown above. `DB_URL`, `DB_USER_NAME`, `DB_PASSWORD`,
`STRIPE_SECRET_KEY` and `STRIPE_WEBHOOK_SECRET` have no usable default
in the application configuration.

For a quick local Windows PowerShell session, for example:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/bookstore?useSSL=false&serverTimezone=UTC"
$env:DB_USER_NAME="bookstore_user"
$env:DB_PASSWORD="your-local-password"
$env:JWT_SECRET="replace-with-a-random-secret-at-least-32-bytes-long"
$env:STRIPE_SECRET_KEY="sk_test_your_key"
$env:STRIPE_WEBHOOK_SECRET="whsec_value_from_stripe_listen"
$env:APP_FRONTEND_URL="http://localhost:5173"
$env:CORS_ALLOWED_ORIGINS="http://localhost:5173"
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
$env:KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
```

From the `backend` directory, run:

```bash
mvn clean spring-boot:run
```

The API should be available at `http://localhost:8080`.

The project includes a `CommandLineRunner` that inserts sample books
when the database is empty. The current initializer seeds book records;
it does not seed the separate `book_prices` records used by the checkout
service, so the sample checkout flow needs that data/configuration
addressed before it can complete successfully.

## 6. Configure and start the frontend

From the `frontend` directory, copy `.env.example` to `.env`:

```bash
cp .env.example .env
```

On Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

The example file contains:

```dotenv
VITE_API_BASE_URL=http://localhost:8080
```

Install the exact dependency versions recorded in the lockfile and start
Vite:

```bash
npm ci
npm run dev
```

The frontend should be available at `http://localhost:5173`.

If the backend is running on another address, change `VITE_API_BASE_URL`
and make sure the backend's `CORS_ALLOWED_ORIGINS` includes the frontend
origin. Restart Vite after changing an environment file.

## 7. Configure Stripe test payments and run the webhook listener

1. Create or use a Stripe account and obtain a **test-mode secret key**.
2. Install the [Stripe CLI](https://docs.stripe.com/stripe-cli) and authenticate:

   ```powershell
   stripe login
   ```

3. Set your Stripe test secret key in the backend environment:

   ```powershell
   $env:STRIPE_SECRET_KEY="sk_test_your_key"
   ```

4. Start the Spring Boot backend.
5. Open a **separate terminal** and start the Stripe webhook listener:

   ```powershell
   stripe listen --events checkout.session.completed,checkout.session.async_payment_succeeded,checkout.session.async_payment_failed --forward-to localhost:8080/api/payments/webhook
   ```

6. The CLI prints a webhook signing secret beginning with `whsec_`. Copy that value into the backend environment. In PowerShell, for example:

   ```powershell
   $env:STRIPE_WEBHOOK_SECRET="whsec_value_printed_by_stripe_listen"
   ```

   Keep the Stripe CLI listener running while testing. If you start a new listener and it prints a different signing secret, update `STRIPE_WEBHOOK_SECRET` to match it.

7. If you set or change `STRIPE_WEBHOOK_SECRET` after the backend has started, restart Spring Boot so it picks up the new value. The backend and Stripe CLI listener must both be running.
8. Open the bookstore, place an order and complete the hosted Stripe Checkout using Stripe test card details. For example:

   ```text
   Card number: 4242 4242 4242 4242
   Expiry: Any future date
   CVC: Any 3 digits
   ZIP/postal code: Any valid value
   ```

   Never use real card details for local testing.

The success page is not the authority for confirming payment. The backend verifies Stripe's signed webhook and updates the order/payment state.

## 8. Run tests

From `backend`:

```bash
mvn test
```

The test resources configure an isolated test database setup and disable
Redis caching. The tests include controller tests using standalone
MockMvc, service tests using Mockito, and repository tests using
`@DataJpaTest`.

## Useful URLs

Purpose URL

---

Frontend `http://localhost:5173`
Backend `http://localhost:8080`
Swagger UI `http://localhost:8080/swagger-ui/index.html`
OpenAPI JSON `http://localhost:8080/v3/api-docs`
