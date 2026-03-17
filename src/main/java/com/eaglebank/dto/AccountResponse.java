package com.eaglebank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AccountResponse {
    private String accountNumber;
    private String sortCode;
    private String name;
    private String accountType;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;
}
