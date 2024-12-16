package com.example.oyster.controller;

import com.example.oyster.dto.TransactionDTO;
import com.example.oyster.model.Card;
import com.example.oyster.model.Station;
import com.example.oyster.repository.CardRepository;
import com.example.oyster.service.AuthenticationService;
import com.example.oyster.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/{cardNumber}")
    public ResponseEntity<Page<TransactionDTO>> getTransactionHistory(
            @PathVariable Long cardNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Card card = cardRepository.findByCardNumber(cardNumber).orElseThrow();
        if (!authenticationService.getLoggedUser().equals(card.getUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<TransactionDTO> transactions = transactionService.getAllTransactionsForCard(cardNumber, page, size);
        return ResponseEntity.ok(transactions);
    }
}