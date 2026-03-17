package com.eaglebank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    private String id;

    @Column(nullable = false)
    private long amountPence; // Store in pence

    @Column(nullable = false)
    private String currency = "GBP";

    @Column(nullable = false)
    private String type; // deposit or withdrawal

    private String reference;

    @ManyToOne
    @JoinColumn(name = "account_number", nullable = false)
    private Account account;

    @CreationTimestamp
    private LocalDateTime createdTimestamp;

    // Helper method to get amount as BigDecimal
    public BigDecimal getAmount() {
        return BigDecimal.valueOf(amountPence).divide(BigDecimal.valueOf(100));
    }

    // Helper method to set amount from BigDecimal
    public void setAmount(BigDecimal amount) {
        this.amountPence = amount.multiply(BigDecimal.valueOf(100)).longValue();
    }
}
