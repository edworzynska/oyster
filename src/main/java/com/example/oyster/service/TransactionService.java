package com.example.oyster.service;

import com.example.oyster.dto.TransactionDTO;
import com.example.oyster.dto.TransactionMapper;
import com.example.oyster.model.Card;
import com.example.oyster.model.DailyCap;
import com.example.oyster.model.Station;
import com.example.oyster.model.Transaction;
import com.example.oyster.repository.CardRepository;
import com.example.oyster.repository.DailyCapRepository;
import com.example.oyster.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Autowired
    private DailyCapRepository dailyCapRepository;

    @Transactional
    public TransactionDTO tapIn(Long cardNumber, Station startStation) {
        Card card = cardRepository.findByCardNumber(cardNumber);

        Transaction transaction = new Transaction();
        transaction.setCard(card);
        transaction.setStartStation(startStation);
        transaction.setStartTime(LocalDateTime.now());
        transactionRepository.save(transaction);

        return transactionMapper.toDTO(transaction);
    }

    @Transactional
    public TransactionDTO tapOut(Long cardNumber, Station endStation) {
        Card card = cardRepository.findByCardNumber(cardNumber);

        Transaction transaction = transactionRepository.findFirstByCardAndEndStationIsNullOrderByStartTimeDesc(card)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        transaction.setEndStation(endStation);
        transaction.setEndTime(LocalDateTime.now());

        BigDecimal fare = fareService.calculateFare(transaction.getStartStation(), endStation);

        BigDecimal adjustedFare = applyDailyCap(card, fare, transaction.getStartStation(), endStation);
        transaction.setFare(adjustedFare);

        transactionRepository.save(transaction);

        return transactionMapper.toDTO(transaction);
    }

    private BigDecimal applyDailyCap(Card card, BigDecimal fare, Station startStation, Station endStation) {
        int startZone = startStation.getZone();
        int endZone = endStation.getZone();

        BigDecimal dailyCap = dailyCapRepository
                .findByStartZoneAndEndZone(startZone, endZone)
                .map(DailyCap::getDailyCap)
                .orElseThrow(() -> new IllegalArgumentException("No daily cap found for the given zones."));

        LocalDate today = LocalDate.now();
        List<Transaction> todaysTransactions = transactionRepository.findAllByCardAndStartTimeBetween(
                card, today.atStartOfDay(), today.plusDays(1).atStartOfDay()
        );

        BigDecimal totalDailyFare = todaysTransactions.stream()
                .map(Transaction::getFare)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDailyFare.add(fare).compareTo(dailyCap) > 0) {
            return dailyCap.subtract(totalDailyFare);
        }

        return fare;
    }
}