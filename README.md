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
