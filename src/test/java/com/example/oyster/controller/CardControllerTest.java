package com.example.oyster.controller;

import com.example.oyster.dto.CardDTO;
import com.example.oyster.model.Card;
import com.example.oyster.model.User;
import com.example.oyster.repository.CardRepository;
import com.example.oyster.repository.UserRepository;
import com.example.oyster.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AuthenticationService authenticationService;

    private User testUser;
    private Card testCard;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("johndoe@example.com");
        testUser.setPassword("password123!");
        userRepository.save(testUser);

        testCard = new Card();
        testCard.setCardNumber(123456789012L);
        testCard.setBalance(BigDecimal.valueOf(100.00));
        testCard.setUser(testUser);
        cardRepository.save(testCard);
    }

    @Test
    @WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testGetCardByIdOwnedByUser() throws Exception {
        mockMvc.perform(get("/api/cards/id/{id}", testCard.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testCard.getId().intValue())))
                .andExpect(jsonPath("$.cardNumber", is(testCard.getCardNumber())))
                .andExpect(jsonPath("$.userId", is(testUser.getId().intValue())));
    }

    @Test
    @WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testGetCardByIdNotOwnedByUser() throws Exception {
        User anotherUser = new User();
        anotherUser.setFirstName("Jane");
        anotherUser.setLastName("Smith");
        anotherUser.setEmail("janesmith@example.com");
        anotherUser.setPassword("password123!");
        userRepository.save(anotherUser);

        Card anotherCard = new Card();
        anotherCard.setCardNumber(987654321098L);
        anotherCard.setBalance(BigDecimal.valueOf(200.00));
        anotherCard.setUser(anotherUser);
        cardRepository.save(anotherCard);

        mockMvc.perform(get("/api/cards/id/{id}", anotherCard.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testGetCardByCardNumberValidCardOwnedByUser() throws Exception {
        mockMvc.perform(get("/api/cards/number/{cardNumber}", testCard.getCardNumber())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testCard.getId().intValue())))
                .andExpect(jsonPath("$.cardNumber", is(testCard.getCardNumber())))
                .andExpect(jsonPath("$.userId", is(testUser.getId().intValue())));
    }

    @Test
    @WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testCreateNewCardRegisteredUser() throws Exception {
        mockMvc.perform(post("/api/cards/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.cardNumber").exists());
    }
    @Test
    void testCreateNewCardAnonymousUser() throws Exception {
        mockMvc.perform(post("/api/cards/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardNumber").exists());
    }

    @Test
    @WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testRegisterCard() throws Exception {
        Card unregisteredCard = new Card();
        unregisteredCard.setCardNumber(111222333444L);
        unregisteredCard.setBalance(BigDecimal.valueOf(50.00));
        cardRepository.save(unregisteredCard);

        mockMvc.perform(post("/api/cards/number/{cardNumber}", unregisteredCard.getCardNumber())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.cardNumber", is(unregisteredCard.getCardNumber())));
    }

    @Test
    @WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddBalance_ValidAmount() throws Exception {
        mockMvc.perform(put("/api/cards/{cardNumber}", testCard.getCardNumber())
                        .param("amount", "50.00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Card updatedCard = cardRepository.findById(testCard.getId()).orElseThrow();
        BigDecimal expectedBalance = BigDecimal.valueOf(150.00); // 100 + 50
        assert updatedCard.getBalance().compareTo(expectedBalance) == 0;
    }

    @Test
    @WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddBalance_InvalidAmount() throws Exception {
        mockMvc.perform(put("/api/cards/{cardNumber}", testCard.getCardNumber())
                        .param("amount", "-10.00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddBalance_InvalidCardNumber() throws Exception {
        mockMvc.perform(put("/api/cards/{cardNumber}", 99L)  // Non-existent card number
                        .param("amount", "10.00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}