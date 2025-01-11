package com.example.oyster.controller;

import com.example.oyster.dto.TransactionDTO;
import com.example.oyster.model.Card;
import com.example.oyster.model.Station;
import com.example.oyster.model.TransactionType;
import com.example.oyster.model.User;
import com.example.oyster.repository.CardRepository;
import com.example.oyster.service.AuthenticationService;
import com.example.oyster.service.TransactionService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CardRepository cardRepository;

    @PostMapping("/{cardNumber}/tapIn")
    public ResponseEntity<TransactionDTO> tapIn(
            @PathVariable Long cardNumber,
            @RequestBody Station startStation) {
        TransactionDTO transaction = transactionService.tapIn(cardNumber, startStation);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PostMapping("/{cardNumber}/tapOut")
    public ResponseEntity<TransactionDTO> tapOut(
            @PathVariable Long cardNumber,
            @RequestBody Station endStation) {
        TransactionDTO transaction = transactionService.tapOut(cardNumber, endStation);
        return ResponseEntity.ok(transaction);
    }
    @GetMapping("/card/{cardNumber}")
    public ResponseEntity<Page<TransactionDTO>> getTransactionHistory(
            @PathVariable Long cardNumber,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TransactionDTO> transactions;
        if (startDate != null && endDate != null) {
            transactions = transactionService.getAllTransactionsForCardWithinDateRange(cardNumber, startDate, endDate, page, size);
        } else {
            transactions = transactionService.getAllTransactionsForCard(cardNumber, page, size);
        }
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long transactionId) {
        User loggedUser = authenticationService.getLoggedUser();

        TransactionDTO transactionDTO = transactionService
                .getTransactionDetails(transactionId, loggedUser);

        return ResponseEntity.ok(transactionDTO);
    }

}