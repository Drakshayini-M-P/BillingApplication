package com.example.billingbackend.repository;

import com.example.billingbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA will automatically create a query for us
    // that finds a user by their email address.
    Optional<User> findByEmail(String email);
}