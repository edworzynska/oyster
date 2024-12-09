package com.example.oyster.service;

import com.example.oyster.dto.UserDTO;
import com.example.oyster.model.User;
import com.example.oyster.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createsUser() {
        String firstName = "First Test";
        String lastName = "Last Test";
        String email = "test@email.com";
        String password = "Password1!";

        UserDTO test = userService.createUser(new UserDTO(firstName, lastName, email, password));

        assertNotNull(test);
        assertTrue(userRepository.existsById(test.getId()));
        assertEquals(firstName, test.getFirstName());
        assertNotNull(test.getCreationDate());
        System.out.println(test.getCreationDate());
        System.out.println(test.getPassword());

    }
    @Test
    void throwsAnErrorIfCreatingUserWithInvalidEmailAddress() {
        String firstName = "First Test";
        String lastName = "Last Test";
        String email = "test";
        String password = "Password1!";

        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()->userService.createUser(new UserDTO(firstName, lastName, email, password)));
        assertEquals("Provided email address is invalid!", e.getMessage());
    }
    @Test
    void throwsAnErrorIfCreatingUserBlankEmailAddress() {
        String firstName = "First Test";
        String lastName = "Last Test";
        String email = "    ";
        String password = "Password1!";

        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()->userService.createUser(new UserDTO(firstName, lastName, email, password)));
        assertEquals("Provided email address is invalid!", e.getMessage());
    }
    @Test
    void throwsAnErrorIfEmailAddressAlreadyInUse() {
        String firstName = "First Test";
        String lastName = "Last Test";
        String email = "test@test";
        String password = "Password1!";
        UserDTO user = userService.createUser(new UserDTO(firstName, lastName, email, password));

        EntityExistsException e = assertThrows(EntityExistsException.class, ()->userService.createUser(new UserDTO(firstName, lastName, email, password)));
        assertEquals("An account with this email address already exists!", e.getMessage());
    }
    @Test
    void throwsAnErrorIfCreatingUserWithBlankFirstName() {
        String firstName = "     ";
        String lastName = "Last Test";
        String email = "test@test";
        String password = "Password1!";

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->userService.createUser(new UserDTO(firstName, lastName, email, password)));
        assertEquals("First name cannot be empty", e.getMessage());
    }
    @Test
    void throwsAnErrorIfCreatingUserWithEmptyFirstName() {
        String firstName = "";
        String lastName = "Last Test";
        String email = "test@test";
        String password = "Password1!";

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->userService.createUser(new UserDTO(firstName, lastName, email, password)));
        assertEquals("First name cannot be empty", e.getMessage());
    }
    @Test
    void throwsAnErrorIfCreatingUserWithBlankLastName() {
        String firstName = "First Name";
        String lastName = "   ";
        String email = "test@test";
        String password = "Password1!";

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->userService.createUser(new UserDTO(firstName, lastName, email, password)));
        assertEquals("Last name cannot be empty", e.getMessage());
    }
    @Test
    void throwsAnErrorIfCreatingUserWithEmptyLastName() {
        String firstName = "First Name";
        String lastName = "";
        String email = "test@test";
        String password = "Password1!";

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->userService.createUser(new UserDTO(firstName, lastName, email, password)));
        assertEquals("Last name cannot be empty", e.getMessage());
    }

    @Test
    void throwsAnErrorIfCreatingUserWithEmptyPassword() {
        String firstName = "First Name";
        String lastName = "Last Name";
        String email = "test@test";
        String password = "";

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->userService.createUser(new UserDTO(firstName, lastName, email, password)));
        String weakPasswordMessage = "Password must be at least 8 characters long, must contain at least one special character, one letter and one number!";
        assertEquals(weakPasswordMessage, e.getMessage());
    }
    @Test
    void throwsAnErrorIfCreatingUserWithWeakPassword() {
        String firstName = "First Name";
        String lastName = "Last Name";
        String email = "test@test";
        String password = "pass";

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->userService.createUser(new UserDTO(firstName, lastName, email, password)));
        String weakPasswordMessage = "Password must be at least 8 characters long, must contain at least one special character, one letter and one number!";
        assertEquals(weakPasswordMessage, e.getMessage());
    }
    @Test
    void throwsAnErrorIfCreatingUserWithWeakPassword2() {
        String firstName = "First Name";
        String lastName = "Last Name";
        String email = "test@test";
        String password = "password";

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->userService.createUser(new UserDTO(firstName, lastName, email, password)));
        String weakPasswordMessage = "Password must be at least 8 characters long, must contain at least one special character, one letter and one number!";
        assertEquals(weakPasswordMessage, e.getMessage());
    }
    @Test
    void throwsAnErrorIfCreatingUserWithWeakPassword3() {
        String firstName = "First Name";
        String lastName = "Last Name";
        String email = "test@test";
        String password = "password1";

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->userService.createUser(new UserDTO(firstName, lastName, email, password)));
        String weakPasswordMessage = "Password must be at least 8 characters long, must contain at least one special character, one letter and one number!";
        assertEquals(weakPasswordMessage, e.getMessage());
    }
    @Test
    void throwsAnErrorIfCreatingUserWithWeakPassword4() {
        String firstName = "First Name";
        String lastName = "Last Name";
        String email = "test@test";
        String password = "pass@word";

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->userService.createUser(new UserDTO(firstName, lastName, email, password)));
        String weakPasswordMessage = "Password must be at least 8 characters long, must contain at least one special character, one letter and one number!";
        assertEquals(weakPasswordMessage, e.getMessage());
    }
}