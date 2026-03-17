package com.eaglebank.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String userId;

    public AuthResponse(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }
}
