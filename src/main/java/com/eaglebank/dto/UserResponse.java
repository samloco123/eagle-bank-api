package com.eaglebank.dto;

import java.time.LocalDateTime;

import com.eaglebank.model.Address;

import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String name;
    private Address address;
    private String phoneNumber;
    private String email;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;
}
