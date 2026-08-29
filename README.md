# Customer Transactions API

A Spring Boot REST API for managing customer transactions.

This project implements the Customer Transactions coding exercise using Java 17, Spring Boot, Spring Data JPA, and an H2 embedded database.

## Technologies Used

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- Maven Wrapper
- JUnit 5
- Spring Boot Test

---

# Quick Start

## Prerequisites

- Java 17
- Git

The project includes the Maven Wrapper, so Maven does not need to be installed separately.

Check the Java version:

```bash
java -version
```

## Build and Test

### Windows

```powershell
.\mvnw.cmd clean test
```

### Linux/macOS

```bash
./mvnw clean test
```

The project must build successfully and all tests must pass.

## Start the Application

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

### Linux/macOS

```bash
./mvnw spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

No separate Maven or database installation is required.

---

# Features

The application supports four transaction operations:

1. Create a transaction
2. Get a transaction by transaction ID
3. Update transaction status
4. Get all transactions for a customer

---

# Testing

Automated integration tests are implemented using **JUnit 5, Spring Boot Test, and TestRestTemplate**.

The tests cover:

- Application context loading
- Successful transaction creation
- Invalid transaction validation
- Duplicate transaction ID
- Transaction not found
- Transaction status update
- Invalid status transition
- Retrieving transactions for a customer

## Test Run

The complete test suite was executed using:

```powershell
.\mvnw.cmd clean test
```

Actual test result:

```text
[INFO] Results:

[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  11.537 s
[INFO] Finished at: 2026-08-30T03:24:12+05:30
[INFO] ------------------------------------------------------------------------
```

The project successfully builds and all tests pass with zero failures and zero errors.

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

### Request

```http
POST /api/transactions
```

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

HTTP Status:

```text
201 Created
```

New transactions always start with `PENDING` status.

The client does not provide the initial status during creation.

---

## 2. Get Transaction

### Request

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

HTTP Status:

```text
200 OK
```

If the transaction does not exist:

```text
404 Not Found
```

---

## 3. Update Transaction Status

### Request

```http
PATCH /api/transactions/TXN001/status
```

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

HTTP Status:

```text
200 OK
```

---

## 4. Get Customer Transactions

### Request

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

HTTP Status:

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

Every newly created transaction starts with:

```text
PENDING
```

The client cannot create a transaction directly with `COMPLETED`, `FAILED`, or `CANCELLED` status.

Status changes are performed using the dedicated status-update endpoint.

## Status Transitions

Supported transitions are:

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

Handles HTTP requests and responses and delegates operations to the service layer.

### Service

Contains the main transaction business logic, including:

- Duplicate transaction validation
- Transaction creation
- Initial status assignment
- Status transition validation
- Transaction retrieval
- Customer transaction retrieval

### Repository

Uses Spring Data JPA to perform database operations.

### Entity

The `Transaction` entity represents transaction data stored in the database.

### DTO

DTOs separate API request data from the database entity and are used for:

- Creating transactions
- Updating transaction status

### Enums

Enums define the supported:

- Currencies
- Transaction types
- Transaction statuses

### Exception

Custom exceptions and a global exception handler provide appropriate HTTP error responses.

---

# Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.example.transactionstarter
│   │       ├── controller
│   │       │   └── TransactionController.java
│   │       ├── dto
│   │       │   ├── CreateTransactionRequest.java
│   │       │   └── UpdateStatusRequest.java
│   │       ├── entity
│   │       │   └── Transaction.java
│   │       ├── enums
│   │       │   ├── Currency.java
│   │       │   ├── TransactionStatus.java
│   │       │   └── TransactionType.java
│   │       ├── exception
│   │       │   ├── DuplicateTransactionException.java
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   └── TransactionNotFoundException.java
│   │       ├── repository
│   │       │   └── TransactionRepository.java
│   │       └── service
│   │           └── TransactionService.java
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

---

# Database

The application uses the H2 embedded database provided by the starter project.

Database configuration:

```text
src/main/resources/application.yml
```

No separate database installation is required.

The H2 database is used for local development and testing.

---

# Supported Values

## Currencies

```text
INR
USD
EUR
GBP
```

## Transaction Types

```text
PAYMENT
REFUND
TRANSFER
```

## Transaction Statuses

```text
PENDING
COMPLETED
FAILED
CANCELLED
```

---

# Clean Clone Verification

The project was verified from a clean clone of the repository.

The verification process was:

```text
1. Clone the repository
2. Run the test suite
3. Confirm all tests pass
4. Start the application
5. Verify the REST APIs
```

The project can be built and tested using the Maven Wrapper without installing Maven separately.

---

# Assumptions

The following assumptions were made for the exercise:

1. Transaction IDs are unique.
2. Transaction IDs and customer IDs are represented as strings.
3. Transaction amounts must be greater than zero.
4. Supported currencies are INR, USD, EUR, and GBP.
5. Supported transaction types are PAYMENT, REFUND, and TRANSFER.
6. New transactions always start with `PENDING`.
7. `COMPLETED` and `CANCELLED` are final states.
8. H2 is used because it is provided by the starter project.
9. Authentication and authorization are outside the scope of this exercise.

---

# Limitations

The implementation is focused on the requirements of the coding exercise.

Current limitations include:

- No authentication or authorization.
- No pagination for customer transactions.
- No transaction audit/history.
- H2 is used instead of a production database.
- No API rate limiting.

---

# Possible Improvements

For a production-ready implementation, the following could be added:

- Authentication and authorization
- Pagination and sorting
- Transaction history/audit logging
- PostgreSQL or MySQL for production
- Database migrations using Flyway or Liquibase
- OpenAPI/Swagger documentation
- Structured logging and monitoring
- More advanced domain-specific validation
- Concurrency handling for simultaneous transaction updates

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