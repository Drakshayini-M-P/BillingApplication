package com.example.billingbackend.repository;

import com.example.billingbackend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // We'll need this for the "customers today" count later
    long countByCreatedAtAfter(LocalDateTime date);
}