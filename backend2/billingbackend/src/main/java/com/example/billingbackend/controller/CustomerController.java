package com.example.billingbackend.controller;

import com.example.billingbackend.exception.ResourceNotFoundException;
import com.example.billingbackend.model.Customer;
import com.example.billingbackend.model.User; // <-- Import User
import com.example.billingbackend.repository.CustomerRepository;
import com.example.billingbackend.repository.UserRepository; // <-- Import UserRepository
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; // <-- Import Transactional
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository; // <-- Inject the UserRepository

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerRepository.findAll());
    }

    @PostMapping
    public Customer createCustomer(@Valid @RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @Valid @RequestBody Customer customerDetails) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());
        
        Customer updatedCustomer = customerRepository.save(customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    // --- THIS IS THE CRITICAL FIX ---
    @DeleteMapping("/{id}")
    @Transactional // Ensures both deletes happen together or not at all
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        // 1. Find the customer to be deleted.
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        // 2. Get the associated User from the customer.
        User user = customer.getUser();

        // 3. Delete the customer record.
        customerRepository.delete(customer);
        
        // 4. If there is a linked user, delete the user record as well.
        if (user != null) {
            userRepository.delete(user);
        }
        
        return ResponseEntity.noContent().build();
    }
}