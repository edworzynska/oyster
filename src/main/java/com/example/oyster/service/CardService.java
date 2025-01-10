package com.example.oyster.service;

import com.example.oyster.dto.CardDTO;
import com.example.oyster.dto.CardMapper;
import com.example.oyster.model.Card;
import com.example.oyster.model.User;
import com.example.oyster.repository.CardRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.UUID;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private TransactionService transactionService;

    public CardDTO getCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("card not found"));

        return cardMapper.toDTO(card);
    }

    public CardDTO getCardByCardNumber(Long cardNumber){
        Card card = cardRepository.findByCardNumber(cardNumber).orElseThrow(()->
                new EntityNotFoundException("card not found"));
        return cardMapper.toDTO(card);
    }

    public List<CardDTO> getCardsByUser(User user){
        List<Card> cards = cardRepository.findByUserId(user.getId());
        return cards.stream().map(cardMapper::toDTO).toList();
    }

    @Transactional
    public CardDTO createRegisteredCard(User user){
        Card card = new Card(generateUniqueCardNumber());
        card.setUser(user);
        cardRepository.save(card);

        return cardMapper.toDTO(card);
    }

    @Transactional
    public CardDTO createUnregisteredCard(){
        Card card = new Card(generateUniqueCardNumber());
        cardRepository.save(card);

        return cardMapper.toDTO(card);
    }

    private Long generateUniqueCardNumber(){
        Long cardNumber;
        do {
            UUID uuid = UUID.randomUUID();
            cardNumber =  Math.abs(uuid.getMostSignificantBits() % 1000000000000L);
        }
        while (cardRepository.existsByCardNumber(cardNumber) || cardNumber.toString().length() != 12);

        return cardNumber;
    }

    @Transactional
    public CardDTO registerCard(Long cardNumber, User user){
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new IllegalArgumentException("invalid card number"));
        if (card.getUser() != null){
            throw new IllegalArgumentException("invalid card number");
        }
        card.setUser(user);
        cardRepository.save(card);
        return cardMapper.toDTO(card);
    }

    @Transactional
    public void addBalance(Long cardNumber, BigDecimal amount){
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new InvalidParameterException("invalid card number"));
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 ||
                amount.scale() > 2) {
            throw new InvalidParameterException("invalid top up value");
        }
        if (!card.getIsActive()){
            throw new InvalidParameterException("The card is inactive!");
        }

        card.setBalance(card.getBalance().add(amount));
        cardRepository.save(card);

        transactionService.topUp(card, amount);
    }

    @Transactional
    public void blockCard(Long cardNumber){
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new InvalidParameterException("invalid card number"));
        if (!card.getIsActive()){
            throw new InvalidParameterException("card is already blocked");
        }

        card.setIsActive(false);

        cardRepository.save(card);
    }
}
