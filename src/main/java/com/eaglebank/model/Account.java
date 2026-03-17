package com.eaglebank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "accounts")
@Data
public class Account {
    @Id
    private String accountNumber;

    @Column(nullable = false)
    private String sortCode = "10-10-10";

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String accountType = "personal";

    @Column(nullable = false)
    private long balancePence; // Store in pence to avoid floating-point errors

    @Column(nullable = false)
    private String currency = "GBP";

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    @CreationTimestamp
    private LocalDateTime createdTimestamp;

    @UpdateTimestamp
    private LocalDateTime updatedTimestamp;

    // Helper method to get balance as BigDecimal
    public BigDecimal getBalance() {
        return BigDecimal.valueOf(balancePence).divide(BigDecimal.valueOf(100));
    }

    // Helper method to set balance from BigDecimal
    public void setBalance(BigDecimal balance) {
        this.balancePence = balance.multiply(BigDecimal.valueOf(100)).longValue();
    }
}
