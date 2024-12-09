package com.example.oyster.dto;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Value
public class CardDTO {
    Long id;
    Long cardNumber;
    Long userId;
    LocalDateTime issuedAt;
    BigDecimal balance;

}
