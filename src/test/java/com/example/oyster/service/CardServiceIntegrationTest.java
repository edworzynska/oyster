package com.example.oyster.service;

import com.example.oyster.dto.CardDTO;
import com.example.oyster.model.User;
import com.example.oyster.repository.CardRepository;
import com.example.oyster.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CardServiceIntegrationTest {

    @Autowired
    private CardService cardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("email@email");
        testUser.setPassword("password1@");
        testUser.setFirstName("First Name");
        testUser.setLastName("Last Name");
        userRepository.save(testUser);
    }

    @Test
    void canCreateNewRegisteredCardWithUniqueNumber() {
        CardDTO cardDTO = cardService.createRegisteredCard(testUser);
        assertNotNull(cardDTO.getCardNumber());
        System.out.println(cardDTO.getCardNumber());
        assertEquals(12, cardDTO.getCardNumber().toString().length());
        assertTrue(cardRepository.existsById(cardDTO.getId()));
        assertEquals(testUser.getId(), cardDTO.getUserId());
    }

    @Test
    void createsUnregisteredCardWithUniqueNumber() {
        CardDTO cardDTO = cardService.createUnregisteredCard();
        assertNotNull(cardDTO.getCardNumber());
        System.out.println(cardDTO.getCardNumber());
        assertEquals(12, cardDTO.getCardNumber().toString().length());
        assertTrue(cardRepository.existsById(cardDTO.getId()));
        assertNull(cardDTO.getUserId());
    }

    @Test
    void findsCardByItsId() {
        CardDTO cardDTO = cardService.createRegisteredCard(testUser);
        CardDTO foundCard = cardService.getCard(cardDTO.getId());
        assertNotNull(foundCard);
        assertEquals(cardDTO.getId(), foundCard.getId());
        assertEquals(cardDTO.getCardNumber(), foundCard.getCardNumber());
        assertEquals(cardDTO.getUserId(), foundCard.getUserId());
    }

    @Test
    void findsCardByCardNumber() {
        CardDTO cardDTO = cardService.createRegisteredCard(testUser);
        CardDTO foundCard = cardService.getCardByCardNumber(cardDTO.getCardNumber());
        assertNotNull(foundCard);
        assertEquals(cardDTO.getId(), foundCard.getId());
        assertEquals(cardDTO.getCardNumber(), foundCard.getCardNumber());
    }
    @Test
    void canRegisterMoreCardsForSameUser() {
        CardDTO cardDTO = cardService.createRegisteredCard(testUser);
        CardDTO cardDTO2 = cardService.createRegisteredCard(testUser);
        Long userId = testUser.getId();
        assertEquals(2, cardRepository.findByUserId(userId).size());
    }

    @Test
    void throwsErrorIfUnableToFindCardById() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> cardService.getCard(99L));
        assertEquals("card not found", e.getMessage());
    }

    @Test
    void throwsErrorIfUnableToFindCardByCardNumber() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> cardService.getCardByCardNumber(123456789L));
        assertEquals("card not found", e.getMessage());
    }
}
