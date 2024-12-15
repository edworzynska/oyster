package com.example.oyster.service;

import com.example.oyster.dto.CardDTO;
import com.example.oyster.dto.CardMapper;
import com.example.oyster.dto.TransactionDTO;
import com.example.oyster.dto.TransactionMapper;
import com.example.oyster.model.Card;
import com.example.oyster.model.Station;
import com.example.oyster.model.Transaction;
import com.example.oyster.repository.CardRepository;
import com.example.oyster.repository.FareRepository;
import com.example.oyster.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private FareService fareService;

    @Transactional
    public TransactionDTO tapIn(Long cardNumber, Station startStation){
        Card card = cardRepository.findByCardNumber(cardNumber);

        Transaction transaction = new Transaction();
        transaction.setCard(card);
        transaction.setStartStation(startStation);
        transactionRepository.save(transaction);

        return transactionMapper.toDTO(transaction);
    }
    @Transactional
    public TransactionDTO tapOut(Long cardNumber, Station endStation){
        Card card = cardRepository.findByCardNumber(cardNumber);

        Transaction transaction = transactionRepository.findFirstByCardAndEndStationIsNullOrderByStartTimeDesc(card)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        transaction.setEndStation(endStation);
        transaction.setEndTime(LocalDateTime.now());

        BigDecimal fare = fareService.calculateFare(transaction.getStartStation(), endStation);
        transaction.setFare(fare);

        transactionRepository.save(transaction);

        return transactionMapper.toDTO(transaction);
    }
}
