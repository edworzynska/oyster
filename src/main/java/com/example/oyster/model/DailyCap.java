package com.example.oyster.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Table(name = "DailyCaps")
@Entity
public class DailyCap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer startZone;
    private Integer endZone;
    private BigDecimal dailyCap;
}
