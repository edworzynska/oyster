package com.example.oyster.service;

import com.example.oyster.dto.UserDTO;
import com.example.oyster.dto.UserMapper;
import com.example.oyster.model.User;
import com.example.oyster.repository.UserRepository;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {

        if (userRepository.existsByEmail(userDTO.getEmail().toLowerCase())) {
            throw new EntityExistsException("An account with this email address already exists!");
        }
        if (!validateEmailAddress(userDTO.getEmail())) {
            throw new InvalidParameterException("Provided email address is invalid!");
        }
        if (!validatePassword(userDTO.getPassword()) || userDTO.getPassword().isEmpty()){
            throw new InvalidParameterException("Password must be at least 8 characters long, must contain at least one special character, one letter and one number!");
        }
        userDTO.setEmail(userDTO.getEmail().toLowerCase());
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);

        return userMapper.toDTO(user);
    }

    private Boolean validatePassword (String password) {
        boolean areRequirementsMet = false;
        if (password.length() >= 8) {
            Pattern letter = Pattern.compile("[a-zA-z]");
            Pattern digit = Pattern.compile("[0-9]");
            Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

            Matcher hasLetter = letter.matcher(password);
            Matcher hasDigit = digit.matcher(password);
            Matcher hasSpecial = special.matcher(password);

            if (hasLetter.find() && hasDigit.find() && hasSpecial.find()) {
                areRequirementsMet = true;
            }
        }
        return areRequirementsMet;
    }

    private Boolean validateEmailAddress(String email) {
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }
    public UserDTO getUserDetails(User loggedUser){
        return userMapper.toDTO(loggedUser);
    }
}
