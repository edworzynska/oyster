package com.example.oyster.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class CardDTO {
    Long cardNumber;
    Long userId;
    LocalDateTime issuedAt;

}
