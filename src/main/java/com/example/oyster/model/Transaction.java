package com.example.oyster.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Transactions")
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @ManyToOne
    @JoinColumn(name = "start_station", nullable = true)
    private Station startStation;

    @ManyToOne
    @JoinColumn(name = "end_station", nullable = true)
    private Station endStation;

    @Column(name = "fare", precision = 19, scale = 2, nullable = true)
    private BigDecimal fare;

    @Column(name = "top_up_amount", precision = 19, scale = 2, nullable = true) // Only for top-ups
    private BigDecimal topUpAmount;

    @CreationTimestamp
    @Column(name = "start_time", updatable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = true)
    private LocalDateTime endTime;

}

