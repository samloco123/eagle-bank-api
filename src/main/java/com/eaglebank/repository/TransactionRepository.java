package com.eaglebank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eaglebank.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccount_AccountNumber(String accountNumber);
    Optional<Transaction> findByIdAndAccount_AccountNumber(String id, String accountNumber);
}
