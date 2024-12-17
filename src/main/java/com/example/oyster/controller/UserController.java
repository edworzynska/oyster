package com.example.oyster.controller;

import com.example.oyster.dto.UserDTO;
import com.example.oyster.service.AuthenticationService;
import com.example.oyster.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/")
    public ResponseEntity<UserDTO> getUserDetails() {
        UserDTO userDTO = userService.getUserDetails(authenticationService.getLoggedUser());
        return ResponseEntity.ok(userDTO);
    }
}