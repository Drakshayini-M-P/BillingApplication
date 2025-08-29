package com.example.billingbackend.dto;

// This class models the data your frontend sends: { "email": "...", "password": "..." }
public class LoginRequest {
    private String email;
    private String password;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}