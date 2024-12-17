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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        if (card.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        Optional<Transaction> incompleteTransactionOpt = transactionRepository
                .findFirstByCardAndEndStationIsNullOrderByStartTimeDesc(card);

        if (incompleteTransactionOpt.isPresent()) {
            Transaction incompleteTransaction = incompleteTransactionOpt.get();

            BigDecimal maxFare = fareService.getMaxFare();
            card.setBalance(card.getBalance().subtract(maxFare));

            incompleteTransaction.setFare(maxFare);
            incompleteTransaction.setEndStation(null);
            incompleteTransaction.setEndTime(LocalDateTime.now());
            transactionRepository.save(incompleteTransaction);
        }

        Transaction transaction = new Transaction();
        transaction.setCard(card);
        transaction.setStartStation(startStation);
        transaction.setStartTime(LocalDateTime.now());

        transactionRepository.save(transaction);

        return transactionMapper.toDTO(transaction);
    }

    @Transactional
    public TransactionDTO tapOut(Long cardNumber, Station endStation) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        Transaction transaction = transactionRepository.findFirstByCardAndEndStationIsNullOrderByStartTimeDesc(card)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        transaction.setEndStation(endStation);
        transaction.setEndTime(LocalDateTime.now());

        BigDecimal fare = fareService.calculateFare(transaction.getStartStation(), endStation);

        BigDecimal adjustedFare = applyDailyCap(card, fare, transaction.getStartStation(), endStation);
        transaction.setFare(adjustedFare);
        card.setBalance(card.getBalance().subtract(adjustedFare));

        cardRepository.save(card);
        transactionRepository.save(transaction);

        return transactionMapper.toDTO(transaction);
    }

    private BigDecimal applyDailyCap(Card card, BigDecimal fare, Station startStation, Station endStation) {
        BigDecimal dailyCap = dailyCapRepository
                .findByStartZoneAndEndZone(startStation.getZone(), endStation.getZone())
                .map(DailyCap::getDailyCap)
                .orElseThrow(() -> new IllegalArgumentException("No daily cap found for the given zones."));

        BigDecimal totalDailyFare = transactionRepository.findAllByCardAndStartTimeBetween(
                        card, LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()
                ).stream()
                .map(Transaction::getFare)
                .filter(Objects::nonNull) // Ensure no null values
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalDailyFare.add(fare).compareTo(dailyCap) > 0
                ? dailyCap.subtract(totalDailyFare)
                : fare;
    }

    public Page<TransactionDTO> getAllTransactionsForCard(Long cardNumber, int page, int size) {
        Page<Transaction> transactionPage = transactionRepository.findByCardCardNumber(cardNumber, PageRequest.of(page, size));
        return transactionPage.map(transactionMapper::toDTO);
    }
}