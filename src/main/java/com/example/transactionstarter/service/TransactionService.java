package com.example.transactionstarter.service;

import com.example.transactionstarter.dto.CreateTransactionRequest;
import com.example.transactionstarter.entity.Transaction;
import com.example.transactionstarter.enums.TransactionStatus;
import com.example.transactionstarter.exception.DuplicateTransactionException;
import com.example.transactionstarter.exception.TransactionNotFoundException;
import com.example.transactionstarter.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // 1. Create transaction
    public Transaction createTransaction(
            CreateTransactionRequest request) {

        // Check whether Transaction ID already exists
        if (transactionRepository.existsById(
                request.getTransactionId())) {

            throw new DuplicateTransactionException(
                    request.getTransactionId());
        }

        // Create Transaction entity
        Transaction transaction = new Transaction();

        transaction.setTransactionId(request.getTransactionId());
        transaction.setCustomerId(request.getCustomerId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setTransactionType(request.getTransactionType());

        // Every new transaction starts as PENDING
        transaction.setStatus(TransactionStatus.PENDING);

        return transactionRepository.save(transaction);
    }

    // 2. Get transaction by Transaction ID
    public Transaction getTransaction(String transactionId) {

        return transactionRepository.findById(transactionId)
                .orElseThrow(
                        () -> new TransactionNotFoundException(
                                transactionId));
    }

    // 3. Update transaction status
    public Transaction updateStatus(
            String transactionId,
            TransactionStatus newStatus) {

        Transaction transaction = getTransaction(transactionId);

        validateStatusTransition(
                transaction.getStatus(),
                newStatus);

        transaction.setStatus(newStatus);

        return transactionRepository.save(transaction);
    }

    // 4. Get all transactions for a customer
    public List<Transaction> getCustomerTransactions(
            String customerId) {

        return transactionRepository
                .findByCustomerId(customerId);
    }

    // Business rule for status changes
    private void validateStatusTransition(
            TransactionStatus currentStatus,
            TransactionStatus newStatus) {

        if (currentStatus == TransactionStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Completed transaction cannot be changed");
        }

        if (currentStatus == TransactionStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cancelled transaction cannot be changed");
        }

        if (currentStatus == newStatus) {
            throw new IllegalStateException(
                    "Transaction already has this status");
        }
    }
}