package com.example.billingbackend.repository;

import com.example.billingbackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // We'll need this for the "products today" count later
    long countByCreatedAtAfter(LocalDateTime date);
}