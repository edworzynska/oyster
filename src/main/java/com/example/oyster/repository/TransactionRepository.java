package com.example.oyster.repository;

import com.example.oyster.model.Card;
import com.example.oyster.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCard(Card card);
    Optional<Transaction> findFirstByCardAndEndStationIsNullOrderByStartTimeDesc(Card card);
}
