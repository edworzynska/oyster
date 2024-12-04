package com.example.oyster.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", unique = true, updatable = false, nullable = false)
    private Long cardNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    public Card(Long cardNumber) {
        this.cardNumber = cardNumber;
    }
}

