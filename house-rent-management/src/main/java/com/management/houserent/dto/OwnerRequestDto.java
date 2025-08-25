package com.management.houserent.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OwnerRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100 , message = " Name must be less than 100 characters")
    private String name ;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email Format")
    private String email ;

    @Size(max = 20 , message = "Phone must be less than 20 characters")
    private String phone ;

    public @NotBlank(message = "Name is required")
    @Size(max = 100, message = " Name must be less than 100 characters")
    String getName() {
        return name;
    }

    public void
    setName(@NotBlank(message = "Name is required")
            @Size(max = 100, message = " Name must be less than 100 characters")
            String name) {
        this.name = name;
    }

    public
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email Format")
    String getEmail() {
        return email;
    }

    public void
    setEmail(@NotBlank(message = "Email is required")
             @Email(message = "Invalid Email Format")
             String email) {
        this.email = email;
    }

    public
    @Size(max = 20, message = "Phone must be less than 20 characters")
    String getPhone() {
        return phone;
    }

    public void setPhone(@Size(max = 20, message = "Phone must be less than 20 characters") String phone) {
        this.phone = phone;
    }
}
