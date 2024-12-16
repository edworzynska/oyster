package com.example.oyster.controller;

import com.example.oyster.dto.UserDTO;
import com.example.oyster.model.User;
import com.example.oyster.repository.UserRepository;
import com.example.oyster.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO validUserDTO;
    private UserDTO invalidEmailUserDTO;
    private UserDTO invalidPasswordUserDTO;
    private UserDTO duplicateEmailUserDTO;

    @BeforeEach
    void setUp() {
        validUserDTO = new UserDTO("John", "Doe", "johndoe@example", "Password@123");
        invalidEmailUserDTO = new UserDTO("Jane", "Doe", "invalid-email", "Password@123");
        invalidPasswordUserDTO = new UserDTO("Jane", "Doe", "janedoe@example", "pass");
        duplicateEmailUserDTO = new UserDTO("John", "Doe", "existinguser@example", "Password@123");
    }

    @Test
    void createsUserSuccessfully() throws Exception {

        mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(validUserDTO.getEmail()))
                .andExpect(jsonPath("$.firstName").value(validUserDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(validUserDTO.getLastName()));
    }

    @Test
    void returnsBadRequestIfEmailIsInvalid() throws Exception {

        mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmailUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Provided email address is invalid!")));
    }

    @Test
    void returnsBadRequestIfPasswordIsInvalid() throws Exception {
        mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPasswordUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Password must be at least 8 characters long, must contain at least one special character, one letter and one number!")));
    }

    @Test
    void returnsConflictIfEmailAlreadyExists() throws Exception {
        User user = new User();
        user.setEmail(duplicateEmailUserDTO.getEmail());
        user.setFirstName("name");
        user.setLastName("surname");
        user.setPassword("password!1");
        userRepository.save(user);

        mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmailUserDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("An account with this email address already exists!")));
    }

}