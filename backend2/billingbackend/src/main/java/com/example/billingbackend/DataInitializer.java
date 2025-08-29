package com.example.billingbackend;

import com.example.billingbackend.model.Role;
import com.example.billingbackend.model.User;
import com.example.billingbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create an ADMIN user
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println(">>> Created test user: admin@example.com (Role: ADMIN)");
        }

        // Create an ACCOUNT user
        if (userRepository.findByEmail("account@example.com").isEmpty()) {
            User accountUser = new User();
            accountUser.setEmail("account@example.com");
            accountUser.setPassword(passwordEncoder.encode("password"));
            accountUser.setRole(Role.ACCOUNT);
            userRepository.save(accountUser);
            System.out.println(">>> Created test user: account@example.com (Role: ACCOUNT)");
        }

        // Create a CUSTOMER user
        if (userRepository.findByEmail("customer@example.com").isEmpty()) {
            User customer = new User();
            customer.setEmail("customer@example.com");
            customer.setPassword(passwordEncoder.encode("password"));
            customer.setRole(Role.CUSTOMER);
            userRepository.save(customer);
            System.out.println(">>> Created test user: customer@example.com (Role: CUSTOMER)");
        }
    }
}