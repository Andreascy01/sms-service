# 📱 SMS Service API

A RESTful API built with [Quarkus](https://quarkus.io/) that allows sending, storing, and simulating SMS messages.  
The project demonstrates persistence with Hibernate ORM (Panache), input validation, error handling, unit testing, and API documentation with Swagger/OpenAPI.

---

## 🚀 Features

- **Send messages** with validation (source number, destination number, and content).
- **Store messages** in a PostgreSQL database.
- **Retrieve messages** by ID or list all messages.
- **Filter messages** by status, source number, or destination number.
- **Simulate delivery** (`DELIVERED` or `FAILED`) asynchronously.
- **Error handling** with descriptive responses for invalid requests.
- **API documentation** using Swagger UI (`/q/swagger-ui`).
- **Unit tests** using JUnit 5, RestAssured, and Mockito.

---

## 🛠️ Tech Stack

- Java 17+
- Quarkus (RESTEasy Reactive, Hibernate ORM Panache, Validation, OpenAPI)
- PostgreSQL (via JDBC & Panache ORM)
- JUnit 5 + RestAssured (testing)
- Docker (optional for containerization)

## ⚙️ Running Locally (without Docker Compose)

### Prerequisites

- **Java 17+** installed
- **Maven** installed (or use the included `./mvnw` wrapper)
- **PostgreSQL** running locally or via Docker

---

### 1. Install and start PostgreSQL

Once you have installed PostgreSQL make sure to create a database and user:

sql code:
CREATE DATABASE smsdb;
CREATE USER smsuser WITH PASSWORD 'smspassword';
GRANT ALL PRIVILEGES ON DATABASE smsdb TO smsuser;

### 2. Configure Database Connection

Check the file src/main/resources/application.properties and make sure it matches your Postgres setup:
For example:
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=smsuser
quarkus.datasource.password=smspassword
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/smsdb
quarkus.hibernate-orm.database.generation=update

### 3. Run the Application in Dev Mode

Run the command "./mvnw quarkus:dev" in your vscode terminal
This will:

- Start Quarkus in hot-reload mode (changes in code reload automatically)
- Connect to PostgreSQL
- Expose the API at http://localhost:8080

## 4. Access the API

- **Swagger UI** → [http://localhost:8080/q/swagger-ui](http://localhost:8080/q/swagger-ui)
- **OpenAPI Spec** → [http://localhost:8080/q/openapi](http://localhost:8080/q/openapi)

Endpoints:

- POST /messages → Send a new message
- GET /messages → List all messages (optional filter by status, sourceNumber, destinationNumber)
- GET /messages/{id} → Get a message by ID
- PUT /messages/{id}/simulate → Simulate delivery result manually

---

### 5. Stop the Service

To stop the Quarkus dev mode, press **CTRL + C** or press **q** in the terminal.
