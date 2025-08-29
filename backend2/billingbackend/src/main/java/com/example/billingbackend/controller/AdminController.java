package com.example.billingbackend.controller;

import com.example.billingbackend.dto.AuthResponse;
import com.example.billingbackend.dto.UserDTO;
import com.example.billingbackend.exception.ResourceNotFoundException;
import com.example.billingbackend.model.Role;
import com.example.billingbackend.model.User;
import com.example.billingbackend.repository.UserRepository;
import com.example.billingbackend.security.JwtTokenProvider;
import com.example.billingbackend.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private JwtTokenProvider tokenProvider;
    @Autowired private CustomUserDetailsService userDetailsService;

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserDTO> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> roleUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        String newRoleStr = roleUpdate.get("role");
        Role newRole = Role.valueOf(newRoleStr.toUpperCase());
        
        user.setRole(newRole);
        User updatedUser = userRepository.save(user);
        
        return ResponseEntity.ok(convertToDto(updatedUser));
    }

    @PostMapping("/users/{id}/impersonate")
    public ResponseEntity<?> impersonateUser(@PathVariable Long id) {
        User userToImpersonate = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        UserDetails userDetails = userDetailsService.loadUserByUsername(userToImpersonate.getEmail());
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        String jwt = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    private UserDTO convertToDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        if (user.getCustomer() != null) {
            userDTO.setName(user.getCustomer().getName());
        } else {
            userDTO.setName("N/A");
        }
        return userDTO;
    }
}