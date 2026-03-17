package com.eaglebank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransactionResponse {
    private String id;
    private BigDecimal amount;
    private String currency;
    private String type;
    private String reference;
    private LocalDateTime createdTimestamp;
}
