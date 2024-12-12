package com.example.oyster.service;

import com.example.oyster.model.User;
import com.example.oyster.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    public User getLoggedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()){
            throw new SecurityException("Please log in to proceed.");
        }
        return userRepository.findByEmail(authentication.getName());
    }
}
