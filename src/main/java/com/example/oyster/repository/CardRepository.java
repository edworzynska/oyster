package com.example.oyster.repository;

import com.example.oyster.model.Card;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CardRepository extends JpaRepository<Card, Long> {

    Card findByCardId(Long cardId);
    Card findByCardNumber(Long cardNumber);
    List<Card> findByUserId(Long userId);
    List<Card> findByUser(User user);
    boolean existsByCardNumber(Long cardNumber);
    boolean existsByCardId(Long cardId);
    boolean existsByUserId(Long userId);
}
