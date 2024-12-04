package com.example.oyster.repository;

import com.example.oyster.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    boolean existsByEmail(String email);
}
