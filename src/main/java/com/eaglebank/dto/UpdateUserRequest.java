package com.eaglebank.dto;

import com.eaglebank.model.Address;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;

    @Valid
    private Address address;

    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format")
    private String phoneNumber;

    @Email(message = "Please provide a valid email address")
    private String email;

    private String password;
}
