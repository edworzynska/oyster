package com.example.oyster.repository;

import com.example.oyster.model.Card;
import com.example.oyster.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCard(Card card);
    Optional<Transaction> findFirstByCardAndEndStationIsNullOrderByStartTimeDesc(Card card);
    List<Transaction> findAllByCardAndStartTimeBetween(Card card, LocalDateTime startTime, LocalDateTime endTime);
    Page<Transaction> findByCardCardNumber(Long cardNumber, Pageable pageable);
    Page<Transaction> findAllByCardAndStartTimeBetween(Card card, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);


}
