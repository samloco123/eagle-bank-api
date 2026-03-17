package com.eaglebank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eaglebank.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByUser_Id(String userId);
    Optional<Account> findByAccountNumberAndUser_Id(String accountNumber, String userId);
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
    boolean existsByUser_Id(String userId);
}
