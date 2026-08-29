# Customer Transactions API

A Spring Boot REST API for managing customer transactions.

This project implements the Customer Transactions exercise using Java 17, Spring Boot, Spring Data JPA, and an H2 embedded database.

## Technologies Used

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- Maven
- JUnit 5
- Spring Boot Test

## Features

The application supports the following operations:

1. Create a transaction
2. Get a transaction by transaction ID
3. Update transaction status
4. Get all transactions for a customer

---

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.example.transactionstarter
│   │       ├── controller
│   │       │   └── TransactionController.java
│   │       │
│   │       ├── dto
│   │       │   ├── CreateTransactionRequest.java
│   │       │   └── UpdateStatusRequest.java
│   │       │
│   │       ├── entity
│   │       │   └── Transaction.java
│   │       │
│   │       ├── enums
│   │       │   ├── Currency.java
│   │       │   ├── TransactionStatus.java
│   │       │   └── TransactionType.java
│   │       │
│   │       ├── exception
│   │       │   ├── DuplicateTransactionException.java
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   └── TransactionNotFoundException.java
│   │       │
│   │       ├── repository
│   │       │   └── TransactionRepository.java
│   │       │
│   │       ├── service
│   │       │   └── TransactionService.java
│   │       │
│   │       └── sample
│   │
│   └── resources
│       └── application.yml
│
└── test
    └── java
        └── com.example.transactionstarter
            ├── TransactionStarterApplicationTests.java
            └── TransactionControllerTest.java
```

### Layer Responsibilities

| Layer | Responsibility |
|---|---|
| Controller | Handles REST API requests and responses |
| Service | Contains transaction business logic |
| Repository | Handles database operations |
| Entity | Represents transaction data in the database |
| DTO | Handles API request data and validation |
| Enums | Defines supported currencies, transaction types, and statuses |
| Exception | Handles application-specific errors |
| Test | Contains automated tests |

---

## Transaction Fields

Each transaction contains:

- Transaction ID
- Customer ID
- Amount
- Currency
- Transaction Type
- Transaction Status

### Supported Currencies

```text
INR
USD
EUR
GBP
```

### Supported Transaction Types

```text
PAYMENT
REFUND
TRANSFER
```

### Supported Transaction Statuses

```text
PENDING
COMPLETED
FAILED
CANCELLED
```

---

# API Endpoints

Base URL:

```text
http://localhost:8080/api/transactions
```

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/transactions` | Create a transaction |
| GET | `/api/transactions/{transactionId}` | Get a transaction |
| PATCH | `/api/transactions/{transactionId}/status` | Update transaction status |
| GET | `/api/transactions/customer/{customerId}` | Get all transactions for a customer |

---

## 1. Create Transaction

### Endpoint

```http
POST /api/transactions
```

### Request

```json
{
  "transactionId": "TXN001",
  "customerId": "CUS001",
  "amount": 1000.00,
  "currency": "INR",
  "transactionType": "PAYMENT"
}
```

### Response

```json
{
  "transactionId": "TXN001",
  "customerId": "CUS001",
  "amount": 1000.00,
  "currency": "INR",
  "transactionType": "PAYMENT",
  "status": "PENDING"
}
```

### HTTP Status

```text
201 Created
```

A newly created transaction always starts with `PENDING` status.

The client does not provide the initial transaction status during creation.

---

## 2. Get Transaction

### Endpoint

```http
GET /api/transactions/{transactionId}
```

### Example

```http
GET /api/transactions/TXN001
```

### Response

```json
{
  "transactionId": "TXN001",
  "customerId": "CUS001",
  "amount": 1000.00,
  "currency": "INR",
  "transactionType": "PAYMENT",
  "status": "PENDING"
}
```

### HTTP Status

```text
200 OK
```

If the transaction does not exist:

```text
404 Not Found
```

---

## 3. Update Transaction Status

### Endpoint

```http
PATCH /api/transactions/{transactionId}/status
```

### Example

```http
PATCH /api/transactions/TXN001/status
```

### Request

```json
{
  "status": "COMPLETED"
}
```

### Response

```json
{
  "transactionId": "TXN001",
  "customerId": "CUS001",
  "amount": 1000.00,
  "currency": "INR",
  "transactionType": "PAYMENT",
  "status": "COMPLETED"
}
```

### HTTP Status

```text
200 OK
```

---

## 4. Get Customer Transactions

### Endpoint

```http
GET /api/transactions/customer/{customerId}
```

### Example

```http
GET /api/transactions/customer/CUS001
```

### Response

```json
[
  {
    "transactionId": "TXN001",
    "customerId": "CUS001",
    "amount": 1000.00,
    "currency": "INR",
    "transactionType": "PAYMENT",
    "status": "COMPLETED"
  },
  {
    "transactionId": "TXN002",
    "customerId": "CUS001",
    "amount": 500.00,
    "currency": "INR",
    "transactionType": "REFUND",
    "status": "PENDING"
  }
]
```

### HTTP Status

```text
200 OK
```

---

# Validation Rules

The following validation rules are implemented:

- Transaction ID is required.
- Transaction ID cannot be blank.
- Transaction ID has a maximum length of 50 characters.
- Transaction ID must be unique.
- Customer ID is required.
- Customer ID cannot be blank.
- Customer ID has a maximum length of 50 characters.
- Amount is required.
- Amount must be greater than zero.
- Currency is required.
- Transaction type is required.
- New transactions always start with `PENDING` status.

---

# Business Rules

## Initial Transaction Status

Every newly created transaction starts as:

```text
PENDING
```

The client cannot create a transaction directly with `COMPLETED`, `FAILED`, or `CANCELLED` status.

Status changes are performed using the dedicated status-update endpoint.

## Status Transitions

The following transitions are supported:

```text
PENDING → COMPLETED
PENDING → FAILED
PENDING → CANCELLED
```

`COMPLETED` and `CANCELLED` are treated as final states and cannot be changed.

For example:

```text
COMPLETED → PENDING
```

is rejected.

Similarly:

```text
CANCELLED → COMPLETED
```

is rejected.

## Duplicate Transaction ID

Transaction IDs must be unique.

If a transaction with the same ID already exists, the API returns:

```text
409 Conflict
```

---

# Error Handling

The application uses centralized exception handling.

| Situation | HTTP Status |
|---|---|
| Invalid request / validation failure | 400 Bad Request |
| Invalid status transition | 400 Bad Request |
| Transaction not found | 404 Not Found |
| Duplicate transaction ID | 409 Conflict |

---

# Architecture

The application follows a layered architecture:

```text
Client / Postman
       |
       v
TransactionController
       |
       v
TransactionService
       |
       v
TransactionRepository
       |
       v
H2 Database
```

### Controller

Handles HTTP requests and responses and delegates business operations to the service layer.

### Service

Contains the main transaction business logic, including:

- Duplicate transaction validation
- Transaction creation
- Initial status assignment
- Status transition validation
- Transaction retrieval
- Customer transaction retrieval

### Repository

Uses Spring Data JPA to communicate with the database.

The repository provides standard database operations such as:

- `save()`
- `findById()`
- `findAll()`
- `existsById()`

It also provides customer-based transaction retrieval.

### Entity

The `Transaction` entity represents transaction data stored in the database.

### DTO

DTOs are used to separate API request data from the database entity.

The application uses request DTOs for:

- Creating transactions
- Updating transaction status

### Enums

Enums define the supported:

- Currencies
- Transaction types
- Transaction statuses

### Exception

Custom exceptions and a global exception handler are used to return appropriate HTTP error responses.

---

# Database

The application uses the H2 embedded database provided by the starter project.

Database configuration is located in:

```text
src/main/resources/application.yml
```

No separate database installation is required.

The H2 database is used for local development and testing.

---

# Running the Application

## Prerequisites

- Java 17
- Git

The project includes the Maven Wrapper, so Maven does not need to be installed separately.

Check the Java version:

```bash
java -version
```

---

## Run Tests

### Windows

```cmd
mvnw.cmd clean test
```

### Linux/macOS

```bash
./mvnw clean test
```

---

## Start the Application

### Windows

```cmd
mvnw.cmd spring-boot:run
```

### Linux/macOS

```bash
./mvnw spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

---

# Testing

The project includes automated tests using JUnit 5 and Spring Boot Test.

The tests cover:

- Application context loading
- Successful transaction creation
- Invalid transaction validation
- Duplicate transaction ID
- Transaction not found
- Transaction status update
- Invalid status transition
- Retrieving transactions for a customer

Run all tests using:

```cmd
mvnw.cmd clean test
```

---

# Assumptions

The following assumptions were made for the exercise:

1. Transaction IDs are unique.
2. Transaction IDs and customer IDs are represented as strings.
3. Transaction amounts must be greater than zero.
4. Supported currencies are INR, USD, EUR, and GBP.
5. Supported transaction types are PAYMENT, REFUND, and TRANSFER.
6. New transactions always start with `PENDING` status.
7. `COMPLETED` and `CANCELLED` are treated as final states.
8. H2 is used as the database because it is provided by the starter project.
9. Authentication and authorization are outside the scope of this exercise.

---

# Limitations

The implementation is focused on the requirements of the coding exercise.

Current limitations include:

- No authentication or authorization.
- No pagination for customer transactions.
- No transaction audit/history.
- H2 is used instead of a production database.
- No currency conversion.
- No API rate limiting.

---

# Possible Improvements

For a production-ready implementation, the following improvements could be added:

- Add authentication and authorization.
- Add pagination and sorting.
- Add transaction history/audit logging.
- Use PostgreSQL or MySQL for production.
- Add database migrations using Flyway or Liquibase.
- Add OpenAPI/Swagger documentation.
- Add structured logging and monitoring.
- Add more advanced domain-specific validation.
- Add concurrency handling for simultaneous transaction updates.

---

# Verification

To verify the application:

1. Clone the repository.
2. Run the test suite:

```cmd
mvnw.cmd clean test
```

3. Start the application:

```cmd
mvnw.cmd spring-boot:run
```

4. Test the APIs using Postman.

The application should successfully build, start, and perform all four required transaction operations.

---

# AI Usage

AI assistance was used during development for:

- Understanding the exercise requirements
- Project structure planning
- Implementation guidance
- Testing guidance
- Troubleshooting
- Documentation

Detailed information about AI usage is provided separately in:
```text
AI_USAGE_DISCLOSURE.md
```

---

# License

This project was developed as part of the Customer Transactions coding exercise.