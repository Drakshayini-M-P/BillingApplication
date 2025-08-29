package com.example.billingbackend.controller;

import com.example.billingbackend.dto.DashboardDataDTO;
import com.example.billingbackend.model.Invoice;
import com.example.billingbackend.repository.CustomerRepository;
import com.example.billingbackend.repository.InvoiceRepository;
import com.example.billingbackend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired private CustomerRepository customerRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private InvoiceRepository invoiceRepository;

    @GetMapping("/data")
    public ResponseEntity<DashboardDataDTO> getDashboardData() {
        List<Invoice> allInvoices = invoiceRepository.findAll();
        
        long customerCount = customerRepository.count();
        long productCount = productRepository.count();
        
        // --- THIS IS THE FIX ---
        // The variable is now consistently named 'totalInvoiceCount'.
        long totalInvoiceCount = allInvoices.size();
        
        long invoicesToday = invoiceRepository.countByCreatedAtAfter(LocalDate.now().atStartOfDay());
        long customersToday = customerRepository.countByCreatedAtAfter(LocalDate.now().atStartOfDay());
        long productsToday = productRepository.countByCreatedAtAfter(LocalDate.now().atStartOfDay());
        
        long paidInvoiceCount = allInvoices.stream().filter(inv -> "Paid".equalsIgnoreCase(inv.getPaymentStatus())).count();
        
        // --- THIS IS THE FIX ---
        // This calculation now uses the correct variable name.
        long unpaidInvoiceCount = totalInvoiceCount - paidInvoiceCount;
        
        List<Invoice> recentSells = invoiceRepository.findTop5ByOrderByCreatedAtDesc();
        
        DashboardDataDTO data = new DashboardDataDTO(
            customerCount, productCount, totalInvoiceCount,
            invoicesToday, customersToday, productsToday,
            paidInvoiceCount, unpaidInvoiceCount,
            recentSells
        );
        
        return ResponseEntity.ok(data);
    }
}