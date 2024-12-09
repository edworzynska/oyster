package com.example.oyster.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UserDTO {

    Long id;
    String firstName;
    String lastName;

    @Email(message = "Email is not valid")
    String email;

    LocalDateTime creationDate;

    @JsonIgnore
    String password;

    public UserDTO(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}
