package com.example.oyster.dto;

import com.example.oyster.model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CardMapper {

    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);

    CardDTO toDTO(Card card);

    @Mapping(target = "user", ignore = true)
    Card toEntity(CardDTO cardDTO);
}