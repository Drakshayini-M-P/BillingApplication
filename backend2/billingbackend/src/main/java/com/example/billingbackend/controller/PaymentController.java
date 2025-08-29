package com.example.billingbackend.controller;

import com.example.billingbackend.dto.PaymentRequestDTO;
import com.example.billingbackend.exception.ResourceNotFoundException;
import com.example.billingbackend.model.Customer;
import com.example.billingbackend.model.Invoice;
import com.example.billingbackend.model.Payment;
import com.example.billingbackend.repository.InvoiceRepository;
import com.example.billingbackend.repository.PaymentRepository;
import com.example.billingbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Handles GET /api/payments
     * Fetches a list of payments. The data returned is dependent on the user's role.
     * - Admin/Account users get all payments.
     * - Customer users get only their own payments.
     */
    @GetMapping
    public List<Payment> getAllPayments(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername(); // This is the user's login email

        boolean isAdminOrAccount = userDetails.getAuthorities().stream()
            .anyMatch(auth -> 
                auth.getAuthority().equals("ROLE_ADMIN") ||
                auth.getAuthority().equals("ROLE_ACCOUNT")
            );
        
        if (isAdminOrAccount) {
            return paymentRepository.findAll();
        } else {
            return paymentRepository.findAll().stream()
                .filter(payment -> {
                    Invoice invoice = payment.getInvoice();
                    return invoice != null && invoice.getCustomer() != null && username.equals(invoice.getCustomer().getEmail());
                })
                .collect(Collectors.toList());
        }
    }

    /**
     * Handles POST /api/payments
     * Creates a new payment, updates the associated invoice's status,
     * and sends a payment confirmation email to the customer.
     */
    @PostMapping
    @Transactional
    public Payment createPayment(@RequestBody PaymentRequestDTO paymentRequest) {
        Invoice invoice = invoiceRepository.findById(paymentRequest.getInvoiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + paymentRequest.getInvoiceId()));

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setNotes(paymentRequest.getNotes());
        payment.setPaymentDate(LocalDateTime.now());
        
        invoice.setPaymentStatus("Paid");
        invoiceRepository.save(invoice);

        Payment savedPayment = paymentRepository.save(payment);

        // --- SEND PAYMENT CONFIRMATION EMAIL ---
        try {
            Customer customer = savedPayment.getInvoice().getCustomer();
            if (customer != null && customer.getEmail() != null) {
                String subject = "Payment Confirmation for Invoice #" + savedPayment.getInvoice().getInvoiceId();
                String body = "Hi " + customer.getName() + ",\n\n" +
                              "This is a confirmation that we have received your payment of $" + savedPayment.getAmount() +
                              " for invoice #" + savedPayment.getInvoice().getInvoiceId() + ".\n\n" +
                              "Thank you for your business!\n" +
                              "The DOS-billing Team";
                emailService.sendEmail(customer.getEmail(), subject, body);
            }
        } catch (Exception e) {
            // Log the error but do not fail the transaction if the email fails to send
            System.err.println("Error sending payment confirmation email for payment ID " + savedPayment.getId() + ": " + e.getMessage());
        }

        return savedPayment;
    }
}