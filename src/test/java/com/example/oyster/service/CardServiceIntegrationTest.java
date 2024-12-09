package com.example.oyster.service;

import com.example.oyster.model.User;
import com.example.oyster.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CardServiceIntegrationTest {

    @Autowired
    private CardService cardService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
    }

    @Test
    void canCreateNewCardWithUniqueNumber() {
        cardService.createCard(testUser);
    }
}
