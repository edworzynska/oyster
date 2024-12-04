package com.example.oyster.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cards = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    public void setEmail(String email) {
        if (email == null || email.isEmpty() || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot empty");
        }
        this.email = email;
    }
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.isEmpty() || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        if (lastName == null || lastName.isEmpty() || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        this.lastName = lastName;
    }
}
