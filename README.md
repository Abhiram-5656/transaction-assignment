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

Project Structure

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
