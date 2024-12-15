package com.example.oyster.dto;

import com.example.oyster.model.Station;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class TransactionDTO {
    Long id;
    Long cardId;
    Long cardNumber;
    Station startStation;
    Station endStation;
    BigDecimal fare;
    LocalDateTime startAt;
    LocalDateTime endAt;
}
