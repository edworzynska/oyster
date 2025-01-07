package com.example.oyster.dto;

import com.example.oyster.model.Transaction;
import com.example.oyster.repository.CardRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {

    @Autowired
    CardRepository cardRepository;

    @Mapping(target = "cardId", source = "card.id")
    @Mapping(target = "cardNumber", source = "card.cardNumber")
    @Mapping(target = "startAt", source = "startTime")
    @Mapping(target = "endAt", source = "endTime")
    @Mapping(target = "topUpAmount", source = "topUpAmount")
    @Mapping(target = "transactionType", source = "transactionType")
    public abstract TransactionDTO toDTO(Transaction transaction);

    @Mapping(target = "card", expression = "java(cardRepository.findById(transactionDTO.getCardId()).orElse(null))")
    public abstract Transaction toEntity(TransactionDTO transactionDTO);
}