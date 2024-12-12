package com.example.oyster.repository;

import com.example.oyster.model.Card;
import com.example.oyster.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCard(Card card);
}
