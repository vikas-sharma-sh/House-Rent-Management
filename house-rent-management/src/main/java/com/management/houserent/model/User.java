package com.management.houserent.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name ="users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email",columnNames = "email")
        }
)
public class User {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;


    @Column(nullable = false,length = 120,unique = true)
    private String email ;

    @Column(nullable = false,length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private Role role;

    @Column(name = "created_at" , updatable = false)
    private LocalDateTime createdAt;

    @Column(name ="updated_at" )
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt =LocalDateTime.now();
        this.updatedAt =LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return Id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(String role) {
        try {
            this.role = Role.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role value: " + role);
        }
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
