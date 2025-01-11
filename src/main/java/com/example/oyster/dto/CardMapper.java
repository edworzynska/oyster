package com.example.oyster.dto;

import com.example.oyster.model.Card;
import com.example.oyster.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CardMapper {

    @Autowired
    UserRepository userRepository;

    @Mapping(target = "userId", source = "user.id")
    public abstract CardDTO toDTO(Card card);

    @Mapping(target = "user", expression = "java(userRepository.findById(cardDTO.getUserId()).orElse(null))")
    public abstract Card toEntity(CardDTO cardDTO);
}