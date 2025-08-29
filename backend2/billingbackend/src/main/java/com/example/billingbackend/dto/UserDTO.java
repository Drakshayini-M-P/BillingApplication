package com.example.billingbackend.dto;

import com.example.billingbackend.model.Role;

public class UserDTO {
    private Long id;
    private String email;
    private String name; // We'll get this from the linked Customer profile
    private Role role;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}

    
    // Getters and Setters for all fields...
}