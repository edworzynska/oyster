package com.example.oyster.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "Fares")
@Data
public class Fare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_zone", nullable = false)
    private Integer startZone;

    @Column(name = "end_zone", nullable = false)
    private Integer endZone;

    @Column(name = "is_peak", nullable = false)
    private Boolean isPeak;

    @Column(nullable = false)
    private BigDecimal fare;
}