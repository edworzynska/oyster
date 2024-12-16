package com.example.oyster.service;

import com.example.oyster.dto.TransactionDTO;
import com.example.oyster.model.Card;
import com.example.oyster.model.DailyCap;
import com.example.oyster.model.Fare;
import com.example.oyster.model.Station;
import com.example.oyster.repository.CardRepository;
import com.example.oyster.repository.DailyCapRepository;
import com.example.oyster.repository.FareRepository;
import com.example.oyster.repository.StationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private FareRepository fareRepository;

    @Autowired
    private DailyCapRepository dailyCapRepository;

    @Autowired
    private CardRepository cardRepository;

    private Card card1;
    private Card card2;
    private Station station1;
    private Station station2;
    private Fare fare1;
    private Fare fare2;
    private Fare fare3;
    private Fare fare4;
    private DailyCap dailyCap1;
    private DailyCap dailyCap2;

    @BeforeEach
    void setUp() {
        card1 = new Card();
        card1.setBalance(new BigDecimal(15));
        card1.setCardNumber(123456789123L);
        cardRepository.save(card1);

        card2 = new Card();
        card2.setBalance(BigDecimal.ZERO);
        card2.setCardNumber(111122223333L);
        cardRepository.save(card2);

        station1 = new Station();
        station1.setName("Station 1");
        station1.setZone(1);
        stationRepository.save(station1);
        station2 = new Station();
        station2.setName("Station 2");
        station2.setZone(2);
        stationRepository.save(station2);

        fare1 = new Fare();
        fare1.setStartZone(1);
        fare1.setEndZone(2);
        fare1.setIsPeak(true);
        fare1.setFare(BigDecimal.valueOf(2.50));
        fareRepository.save(fare1);
        fare2 = new Fare();
        fare2.setStartZone(1);
        fare2.setEndZone(2);
        fare2.setIsPeak(false);
        fare2.setFare(BigDecimal.valueOf(2.50));
        fareRepository.save(fare2);
        fare3 = new Fare();
        fare3.setStartZone(2);
        fare3.setEndZone(1);
        fare3.setIsPeak(true);
        fare3.setFare(BigDecimal.valueOf(2.50));
        fareRepository.save(fare3);
        fare4 = new Fare();
        fare4.setStartZone(2);
        fare4.setEndZone(1);
        fare4.setIsPeak(false);
        fare4.setFare(BigDecimal.valueOf(2.50));
        fareRepository.save(fare4);


        dailyCap1 = new DailyCap();
        dailyCap1.setStartZone(1);
        dailyCap1.setEndZone(2);
        dailyCap1.setDailyCap(BigDecimal.valueOf(5.00));
        dailyCapRepository.save(dailyCap1);
        dailyCap2 = new DailyCap();
        dailyCap2.setStartZone(2);
        dailyCap2.setEndZone(1);
        dailyCap2.setDailyCap(BigDecimal.valueOf(5.00));
        dailyCapRepository.save(dailyCap2);
    }

    @Test
    void tapsInSuccessfully() {
        Long cardNumber = card1.getCardNumber();
        TransactionDTO transaction = transactionService.tapIn(cardNumber, station1);
        assertEquals(station1, transaction.getStartStation());
        assertEquals(cardNumber, transaction.getCardNumber());
        assertNotNull(transaction);
        assertEquals(cardNumber, transaction.getCardNumber());
    }

    @Test
    void unableToTapInIfBalanceIsInsufficient() {
        Long cardNumber = card2.getCardNumber();
        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
                transactionService.tapIn(cardNumber, station1));
        assertEquals("Insufficient balance", e.getMessage());
    }

    @Test
    void tapsOutAndUpdatesTransaction() {
        Long cardNumber = card1.getCardNumber();
        TransactionDTO transaction = transactionService.tapIn(cardNumber, station1);

        TransactionDTO transaction2 = transactionService.tapOut(cardNumber, station2);
        assertEquals(station2, transaction2.getEndStation());
        assertEquals(cardNumber, transaction2.getCardNumber());
        assertNotNull(transaction2);
        assertEquals(cardNumber, transaction2.getCardNumber());
        assertEquals(new BigDecimal(2.5), transaction2.getFare());
        assertEquals(transaction.getId(), transaction2.getId());
        assertEquals(new BigDecimal(12.5), card1.getBalance());
    }

    @Test
    void balanceCanGoBelowZero() {
        card1.setBalance(BigDecimal.valueOf(1));
        cardRepository.save(card1);
        Long cardNumber = card1.getCardNumber();
        TransactionDTO transaction = transactionService.tapIn(cardNumber, station1);
        TransactionDTO transaction2 = transactionService.tapOut(cardNumber, station2);

        assertEquals(new BigDecimal(-1.5), card1.getBalance());
    }

    @Test
    void faresCannotGoAboveDailyCap() {
        Long cardNumber = card1.getCardNumber();
        TransactionDTO transaction = transactionService.tapIn(cardNumber, station1);
        TransactionDTO transaction2 = transactionService.tapOut(cardNumber, station2);
        TransactionDTO transaction3 = transactionService.tapIn(cardNumber, station2);
        TransactionDTO transaction4 = transactionService.tapOut(cardNumber, station1);
        TransactionDTO transaction5 = transactionService.tapIn(cardNumber, station1);
        TransactionDTO transaction6 = transactionService.tapOut(cardNumber, station2);

        assertEquals(BigDecimal.valueOf(0.0), transaction6.getFare());

        TransactionDTO transaction7 = transactionService.tapIn(cardNumber, station2);
        TransactionDTO transaction8 = transactionService.tapOut(cardNumber, station1);

        assertEquals(BigDecimal.valueOf(0.0), transaction8.getFare());
        assertEquals(BigDecimal.valueOf(10.0), card1.getBalance());

    }

    @Test
    void throwsEntityNotFoundExceptionIfTappingOutWithoutTappingIn() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () ->
                transactionService.tapOut(card1.getCardNumber(), station1));
        assertEquals("Transaction not found", e.getMessage());
    }
    @Test
    void throwsEntityNotFoundExceptionIfCardNotFound() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () ->
                transactionService.tapIn(444L, station1));
        assertEquals("Card not found", e.getMessage());
    }
}
