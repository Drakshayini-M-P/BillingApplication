package com.example.billingbackend.repository;

import com.example.billingbackend.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // This custom method finds the top 5 most recent invoices
    List<Invoice> findTop5ByOrderByCreatedAtDesc();
    
    // This method counts invoices created today
    long countByCreatedAtAfter(LocalDateTime date);
}