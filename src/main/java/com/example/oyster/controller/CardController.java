package com.example.oyster.controller;

import com.example.oyster.configuration.UserNotAuthenticatedException;
import com.example.oyster.dto.CardDTO;
import com.example.oyster.model.User;
import com.example.oyster.service.AuthenticationService;
import com.example.oyster.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CardService cardService;

    @GetMapping("/id/{id}")
    public ResponseEntity<CardDTO> getCard(@PathVariable Long id) {
        User user = authenticationService.getLoggedUser();
        CardDTO cardDTO = cardService.getCard(id);
        if (cardDTO.getUserId().equals(user.getId())){
            return ResponseEntity.ok(cardDTO);
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @GetMapping("/number/{cardNumber}")
    public ResponseEntity<CardDTO> getCardByCardNumber(@PathVariable Long cardNumber) {
        User user = authenticationService.getLoggedUser();
        CardDTO cardDTO = cardService.getCardByCardNumber(cardNumber);
        if (cardDTO.getUserId().equals(user.getId())){
            return ResponseEntity.ok(cardDTO);
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
    @GetMapping("/")
    public ResponseEntity<List<CardDTO>> getCards() {
        User user = authenticationService.getLoggedUser();
        List<CardDTO> cards = cardService.getCardsByUser(user);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/")
    public ResponseEntity<CardDTO> newCard() {
        try {
            User loggedUser = authenticationService.getLoggedUser();

            CardDTO cardDTO = cardService.createRegisteredCard(loggedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(cardDTO);
        } catch (UserNotAuthenticatedException e) {
            CardDTO cardDTO = cardService.createUnregisteredCard();
            return ResponseEntity.status(HttpStatus.CREATED).body(cardDTO);
        }
    }

    @PostMapping("/number/{cardNumber}")
    public ResponseEntity<CardDTO> registerCard(@PathVariable Long cardNumber) {
        User loggedUser = authenticationService.getLoggedUser();
        CardDTO cardDTO = cardService.registerCard(cardNumber, loggedUser);
        return ResponseEntity.ok(cardDTO);
    }

    @PutMapping("/{cardNumber}")
    public ResponseEntity<Void> addBalance(@PathVariable Long cardNumber,
                                           @RequestParam BigDecimal amount) {
        cardService.addBalance(cardNumber, amount);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/block/{cardNumber}")
    public ResponseEntity<String> blockCard(@PathVariable Long cardNumber) {

        cardService.blockCard(cardNumber);
        return ResponseEntity.ok().build();
    }
}