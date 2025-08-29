package com.example.billingbackend.controller;

import com.example.billingbackend.dto.AuthResponse;
import com.example.billingbackend.dto.LoginRequest;
import com.example.billingbackend.dto.RegistrationRequestDTO;
import com.example.billingbackend.model.Customer;
import com.example.billingbackend.model.Role;
import com.example.billingbackend.model.User;
import com.example.billingbackend.repository.CustomerRepository;
import com.example.billingbackend.repository.UserRepository;
import com.example.billingbackend.security.JwtTokenProvider;
import com.example.billingbackend.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtTokenProvider tokenProvider;
    @Autowired private UserRepository userRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequestDTO registrationRequest) {
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            return new ResponseEntity<>("Email Address is already in use!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setRole(Role.CUSTOMER);
        
        User savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setName(registrationRequest.getName());
        customer.setPhone(registrationRequest.getPhone());
        customer.setEmail(registrationRequest.getEmail());
        customer.setUser(savedUser);
        
        // --- THIS IS THE CRITICAL FIX ---
        // We capture the saved customer entity, which is now fully managed by the database.
        Customer savedCustomer = customerRepository.save(customer);

        // --- SEND WELCOME EMAIL ---
        try {
            String subject = "Welcome to DOS-billing!";
            String body = "Hi " + savedCustomer.getName() + ",\n\n" +
                          "Thank you for registering for the DOS-billing app. We're excited to have you!\n\n" +
                          "You can now log in with your email and password.\n\n" +
                          "The DOS-billing Team";
            // Now we use the fully saved and persistent 'savedCustomer' object.
            emailService.sendEmail(savedCustomer.getEmail(), subject, body);
        } catch (Exception e) {
            // Log the error but do not fail the registration if the email fails.
            System.err.println("Failed to send welcome email to " + savedCustomer.getEmail() + ": " + e.getMessage());
        }

        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
    }
}