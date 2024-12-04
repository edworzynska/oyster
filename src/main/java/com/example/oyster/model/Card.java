package com.example.oyster.model;

import com.example.oyster.configuration.CardNumberUtil;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Cards")
public class Card {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", unique = true, updatable = false, nullable = false)
    private Long cardNumber;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @PrePersist
    protected void onCreate() {
        this.cardNumber = CardNumberUtil.generateUniqueCardNumber();
    }
}

