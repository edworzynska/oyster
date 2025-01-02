package com.example.oyster.controller;

import com.example.oyster.dto.TransactionDTO;
import com.example.oyster.model.*;
import com.example.oyster.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FareRepository fareRepository;

    @Autowired
    private DailyCapRepository dailyCapRepository;


    private Card card;
    private Station startStation;
    private Station endStation;
    private User testUser;
    private Fare fare1;
    private Fare fare2;
    private Fare fare3;
    private Fare fare4;
    private DailyCap dailyCap1;
    private DailyCap dailyCap2;

    @BeforeEach
    void setUp() {
        card = new Card();
        card.setCardNumber(123456789012L);
        card.setBalance(BigDecimal.valueOf(50.00));
        cardRepository.save(card);

        startStation = new Station();
        startStation.setName("Start Station");
        startStation.setZone(1);
        stationRepository.save(startStation);

        endStation = new Station();
        endStation.setName("End Station");
        endStation.setZone(2);
        stationRepository.save(endStation);

        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("johndoe@example.com");
        testUser.setPassword("password123!");
        userRepository.save(testUser);

        fare1 = new Fare();
        fare1.setStartZone(1);
        fare1.setEndZone(2);
        fare1.setIsPeak(true);
        fare1.setFare(BigDecimal.valueOf(2.50));
        fareRepository.save(fare1);
        fare2 = new Fare();
        fare2.setStartZone(1);
        fare2.setEndZone(2);
        fare2.setIsPeak(false);
        fare2.setFare(BigDecimal.valueOf(2.50));
        fareRepository.save(fare2);
        fare3 = new Fare();
        fare3.setStartZone(2);
        fare3.setEndZone(1);
        fare3.setIsPeak(true);
        fare3.setFare(BigDecimal.valueOf(2.50));
        fareRepository.save(fare3);
        fare4 = new Fare();
        fare4.setStartZone(2);
        fare4.setEndZone(1);
        fare4.setIsPeak(false);
        fare4.setFare(BigDecimal.valueOf(2.50));
        fareRepository.save(fare4);


        dailyCap1 = new DailyCap();
        dailyCap1.setStartZone(1);
        dailyCap1.setEndZone(2);
        dailyCap1.setDailyCap(BigDecimal.valueOf(5.00));
        dailyCapRepository.save(dailyCap1);
        dailyCap2 = new DailyCap();
        dailyCap2.setStartZone(2);
        dailyCap2.setEndZone(1);
        dailyCap2.setDailyCap(BigDecimal.valueOf(5.00));
        dailyCapRepository.save(dailyCap2);
    }

    @Test
    @WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void tapInSuccessful() throws Exception {
        mockMvc.perform(post("/api/transactions/" + card.getCardNumber() + "/tapIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startStation)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fare").doesNotExist());
    }

    @Test
    @WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void tapOutSuccessful() throws Exception {
        mockMvc.perform(post("/api/transactions/" + card.getCardNumber() + "/tapIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startStation)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/transactions/" + card.getCardNumber() + "/tapOut")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endStation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fare").exists())
                .andExpect(jsonPath("$.fare").value(BigDecimal.valueOf(2.5)));
    }
    @Test
    @WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void tapInWithCardNotFound() throws Exception {
        mockMvc.perform(post("/api/transactions/99999/tapIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startStation)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Card not found"));
    }

    @Test
    @WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void tapOutNoMatchingTransaction() throws Exception {
        mockMvc.perform(post("/api/transactions/" + card.getCardNumber() + "/tapOut")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endStation)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Transaction not found"));
    }
    @Test
    @WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void tapInWithInsufficientBalance() throws Exception {

        card.setBalance(BigDecimal.ZERO);
        cardRepository.save(card);
        mockMvc.perform(post("/api/transactions/" + card.getCardNumber() + "/tapIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endStation)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance"));
    }
    @Test
    @WithUserDetails(value = "johndoe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testGetTransactionHistory_Pagination() throws Exception {
        card.setUser(testUser);
        cardRepository.save(card);

        mockMvc.perform(get("/api/transactions/card/123456789012"))
                .andExpect(status().isOk());
    }
}