package com.example.oyster.service;

import com.example.oyster.dto.CardDTO;
import com.example.oyster.dto.CardMapper;
import com.example.oyster.model.Card;
import com.example.oyster.model.User;
import com.example.oyster.repository.CardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getsExistingCard() {
        Long cardId = 1L;
        Card card = new Card(123456789L);
        card.setId(cardId);

        CardDTO cardDTO = new CardDTO(cardId, 123456789L, null, null, BigDecimal.ZERO);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardMapper.toDTO(card)).thenReturn(cardDTO);

        CardDTO result = cardService.getCard(cardId);

        assertNotNull(result);
        assertEquals(cardId, result.getId());
        assertEquals(123456789L, result.getCardNumber());
        verify(cardRepository, times(1)).findById(cardId);
        verify(cardMapper, times(1)).toDTO(card);
    }

    @Test
    void testGetCard_NonExistentCard() {
        Long cardId = 1L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> cardService.getCard(cardId));
        assertEquals("card not found", exception.getMessage());
        verify(cardRepository, times(1)).findById(cardId);
    }

    @Test
    void testCreateRegisteredCard() {
        User user = new User();
        user.setId(1L);

        Card card = new Card(123456789L);
        card.setUser(user);

        CardDTO cardDTO = new CardDTO(1L, 123456789L, user.getId(), null, BigDecimal.ZERO);

        when(cardRepository.existsByCardNumber(anyLong())).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDTO(any(Card.class))).thenReturn(cardDTO);

        CardDTO result = cardService.createRegisteredCard(user);

        assertNotNull(result);
        assertEquals(123456789L, result.getCardNumber());
        assertEquals(user.getId(), result.getUserId());
        verify(cardRepository, times(1)).save(any(Card.class));
        verify(cardMapper, times(1)).toDTO(any(Card.class));
    }

    @Test
    void testGenerateUniqueCardNumber() {

        User user = new User();
        user.setId(1L);

        Long generatedCardNumber = 123456789L;
        Card card = new Card(generatedCardNumber);
        card.setId(1L);
        card.setUser(user);

        CardDTO cardDTO = new CardDTO(1L, generatedCardNumber, user.getId(), null, BigDecimal.ZERO);

        when(cardRepository.existsByCardNumber(anyLong())).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDTO(any(Card.class))).thenReturn(cardDTO);

        CardDTO result = cardService.createRegisteredCard(user);

        assertNotNull(result, "CardDTO should not be null");
        assertNotNull(result.getCardNumber(), "Card number should not be null");
        assertEquals(generatedCardNumber, result.getCardNumber(), "Card number does not match");
        assertEquals(user.getId(), result.getUserId(), "User ID does not match");
        verify(cardRepository, times(1)).save(any(Card.class));
        verify(cardMapper, times(1)).toDTO(any(Card.class));
    }
}
