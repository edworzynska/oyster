package com.example.oyster.service;

import com.example.oyster.dto.CardDTO;
import com.example.oyster.dto.CardMapper;
import com.example.oyster.model.Card;
import com.example.oyster.repository.CardRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private final CardMapper cardMapper;

    public CardService(CardRepository cardRepository, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
    }

    public CardDTO getCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("card not found"));

        return cardMapper.toDTO(card);
    }

    public Long generateUniqueCardNumber(){
        Long cardNumber;
        do {
            UUID uuid = UUID.randomUUID();
            cardNumber =  Math.abs(uuid.getMostSignificantBits() % 1000000000000L);
        }
        while (cardRepository.existsByCardNumber(cardNumber));

        return cardNumber;
    }
}
