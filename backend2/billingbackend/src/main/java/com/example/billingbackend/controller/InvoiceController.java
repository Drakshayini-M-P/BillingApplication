package com.example.billingbackend.controller;

import com.example.billingbackend.dto.InvoiceOverviewDTO;
import com.example.billingbackend.dto.InvoiceRequestDTO;
import com.example.billingbackend.exception.ResourceNotFoundException;
import com.example.billingbackend.model.*;
import com.example.billingbackend.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired private InvoiceRepository invoiceRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private InvoiceItemRepository invoiceItemRepository;

    /**
     * Handles GET /api/invoices
     * Fetches a list of invoices. This endpoint is now role-aware.
     * - Admin/Account users get all invoices.
     * - Customer users get only their own invoices.
     */
    @GetMapping
    public List<Invoice> getAllInvoices(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername(); // This is the user's login email

        boolean isAdminOrAccount = userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_ACCOUNT"));
        
        if (isAdminOrAccount) {
            // If user is Admin or Account, return all invoices.
            return invoiceRepository.findAll();
        } else {
            // Otherwise, user is a Customer. Filter to return only their invoices.
            return invoiceRepository.findAll().stream()
                .filter(invoice -> invoice.getCustomer() != null && username.equals(invoice.getCustomer().getEmail()))
                .collect(Collectors.toList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/overview")
    public ResponseEntity<InvoiceOverviewDTO> getInvoiceOverview() {
        // This method correctly fetches and calculates real data for the Admin/Account overview
        List<Invoice> allInvoices = invoiceRepository.findAll();
        long totalInvoiceCount = allInvoices.size();
        BigDecimal totalAmount = allInvoices.stream().map(Invoice::getAmount).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        long paidInvoiceCount = allInvoices.stream().filter(inv -> "Paid".equalsIgnoreCase(inv.getPaymentStatus())).count();
        BigDecimal paidAmount = allInvoices.stream().filter(inv -> "Paid".equalsIgnoreCase(inv.getPaymentStatus())).map(Invoice::getAmount).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        long unpaidInvoiceCount = totalInvoiceCount - paidInvoiceCount;
        BigDecimal unpaidAmount = totalAmount.subtract(paidAmount);
        List<Invoice> recentInvoices = invoiceRepository.findTop5ByOrderByCreatedAtDesc();
        InvoiceOverviewDTO overview = new InvoiceOverviewDTO(totalAmount, paidAmount, unpaidAmount, totalInvoiceCount, paidInvoiceCount, unpaidInvoiceCount, recentInvoices);
        return ResponseEntity.ok(overview);
    }

    @PostMapping
    @Transactional
    public Invoice createInvoice(@Valid @RequestBody InvoiceRequestDTO request) {
        // This method correctly creates an invoice and its line items
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setPaymentStatus(request.getPaymentStatus());
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setInvoiceId("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setItems(new ArrayList<>());
        Invoice savedInvoice = invoiceRepository.save(invoice);
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (InvoiceRequestDTO.InvoiceItemDTO itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId()));
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setProduct(product);
            invoiceItem.setQuantity(itemDto.getQuantity());
            invoiceItem.setUnitPrice(product.getPrice());
            invoiceItem.setInvoice(savedInvoice);
            invoiceItemRepository.save(invoiceItem);
            savedInvoice.getItems().add(invoiceItem);
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }
        savedInvoice.setAmount(totalAmount);
        return invoiceRepository.save(savedInvoice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoiceDetails) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        invoice.setPaymentStatus(invoiceDetails.getPaymentStatus());
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return ResponseEntity.ok(updatedInvoice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        invoiceRepository.delete(invoice);
        return ResponseEntity.noContent().build();
    }
}