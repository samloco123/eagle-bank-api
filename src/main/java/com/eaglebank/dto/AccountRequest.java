package com.eaglebank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccountRequest {
    @NotBlank(message = "Account name is required")
    private String name;
    
    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "personal", message = "Account type must be 'personal'")
    private String accountType;
}
