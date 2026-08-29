package com.example.transactionstarter;

import com.example.transactionstarter.entity.Transaction;
import com.example.transactionstarter.enums.Currency;
import com.example.transactionstarter.enums.TransactionStatus;
import com.example.transactionstarter.enums.TransactionType;
import com.example.transactionstarter.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void cleanDatabase() {
        transactionRepository.deleteAll();
    }

    private String baseUrl() {
        return "http://localhost:" + port + "/api/transactions";
    }

    @Test
    void shouldCreateTransactionSuccessfully() {

        String requestBody = """
                {
                    "transactionId": "TXN001",
                    "customerId": "CUS001",
                    "amount": 1000.00,
                    "currency": "INR",
                    "transactionType": "PAYMENT"
                    
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<Transaction> response =
                restTemplate.postForEntity(
                        baseUrl(),
                        request,
                        Transaction.class
                );

        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "TXN001",
                response.getBody().getTransactionId()
        );

        assertEquals(
                "CUS001",
                response.getBody().getCustomerId()
        );

        assertEquals(
                new BigDecimal("1000.00"),
                response.getBody().getAmount()
        );

        assertEquals(
                TransactionStatus.PENDING,
                response.getBody().getStatus()
        );
    }

    @Test
    void shouldRejectInvalidTransaction() {

        String requestBody = """
                {
                    "transactionId": "",
                    "customerId": "",
                    "amount": -100.00,
                    "currency": "INR",
                    "transactionType": "PAYMENT",
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        baseUrl(),
                        request,
                        String.class
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }

    @Test
    void shouldRejectDuplicateTransactionId() {

        Transaction existingTransaction = new Transaction(
                "TXN001",
                "CUS001",
                new BigDecimal("1000.00"),
                Currency.INR,
                TransactionType.PAYMENT,
                TransactionStatus.PENDING
        );

        transactionRepository.save(existingTransaction);

        String requestBody = """
                {
                    "transactionId": "TXN001",
                    "customerId": "CUS002",
                    "amount": 500.00,
                    "currency": "INR",
                    "transactionType": "PAYMENT"
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        baseUrl(),
                        request,
                        String.class
                );

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );
    }

    @Test
    void shouldReturnNotFoundForUnknownTransaction() {

        ResponseEntity<String> response =
                restTemplate.getForEntity(
                        baseUrl() + "/UNKNOWN999",
                        String.class
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );
    }
    @Test
void shouldUpdateTransactionStatus() {

    Transaction transaction = new Transaction(
            "TXN001",
            "CUS001",
            new BigDecimal("1000.00"),
            Currency.INR,
            TransactionType.PAYMENT,
            TransactionStatus.PENDING
    );

    transactionRepository.save(transaction);

    String requestBody = """
            {
                "status": "COMPLETED"
            }
            """;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request =
            new HttpEntity<>(requestBody, headers);

    ResponseEntity<Transaction> response =
            restTemplate.exchange(
                    baseUrl() + "/TXN001/status",
                    HttpMethod.PATCH,
                    request,
                    Transaction.class
            );

    assertEquals(
            HttpStatus.OK,
            response.getStatusCode()
    );

    assertNotNull(response.getBody());

    assertEquals(
            TransactionStatus.COMPLETED,
            response.getBody().getStatus()
    );
}
@Test
void shouldRejectStatusChangeFromCompletedTransaction() {

    Transaction transaction = new Transaction(
            "TXN002",
            "CUS001",
            new BigDecimal("500.00"),
            Currency.INR,
            TransactionType.PAYMENT,
            TransactionStatus.COMPLETED
    );

    transactionRepository.save(transaction);

    String requestBody = """
            {
                "status": "PENDING"
            }
            """;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request =
            new HttpEntity<>(requestBody, headers);

    ResponseEntity<String> response =
            restTemplate.exchange(
                    baseUrl() + "/TXN002/status",
                    HttpMethod.PATCH,
                    request,
                    String.class
            );

    assertEquals(
            HttpStatus.BAD_REQUEST,
            response.getStatusCode()
    );
}
@Test
void shouldGetAllTransactionsForCustomer() {

    Transaction transaction1 = new Transaction(
            "TXN001",
            "CUS001",
            new BigDecimal("1000.00"),
            Currency.INR,
            TransactionType.PAYMENT,
            TransactionStatus.PENDING
    );

    Transaction transaction2 = new Transaction(
            "TXN002",
            "CUS001",
            new BigDecimal("500.00"),
            Currency.INR,
            TransactionType.REFUND,
            TransactionStatus.COMPLETED
    );

    Transaction transaction3 = new Transaction(
            "TXN003",
            "CUS002",
            new BigDecimal("750.00"),
            Currency.INR,
            TransactionType.PAYMENT,
            TransactionStatus.PENDING
    );

    transactionRepository.save(transaction1);
    transactionRepository.save(transaction2);
    transactionRepository.save(transaction3);

    ResponseEntity<Transaction[]> response =
            restTemplate.getForEntity(
                    baseUrl() + "/customer/CUS001",
                    Transaction[].class
            );

    assertEquals(
            HttpStatus.OK,
            response.getStatusCode()
    );

    assertNotNull(response.getBody());

    assertEquals(
            2,
            response.getBody().length
    );

    assertTrue(
            java.util.Arrays.stream(response.getBody())
                    .allMatch(transaction ->
                            transaction.getCustomerId()
                                    .equals("CUS001"))
    );
}
}
