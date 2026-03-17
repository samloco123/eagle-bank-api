package com.eaglebank.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglebank.dto.TransactionRequest;
import com.eaglebank.dto.TransactionResponse;
import com.eaglebank.exception.ForbiddenException;
import com.eaglebank.exception.NotFoundException;
import com.eaglebank.exception.UnprocessableEntityException;
import com.eaglebank.model.Account;
import com.eaglebank.model.Transaction;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.TransactionRepository;
import com.eaglebank.util.SecurityUtils;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final SecurityUtils securityUtils;
    private final EntityManager entityManager;


    @Transactional
    public TransactionResponse createTransaction(String accountNumber, TransactionRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Bank account was not found"));

        String currentUserId = securityUtils.getCurrentUserId();
        if (!currentUserId.equals(account.getUser().getId())) {
            throw new ForbiddenException("The user is not allowed to access this transaction");
        }

        // Convert amount to pence for storage
        long amountPence = request.getAmount().multiply(BigDecimal.valueOf(100)).longValue();

        // Check for sufficient funds for withdrawals
        if ("withdrawal".equals(request.getType()) && account.getBalancePence() < amountPence) {
            throw new UnprocessableEntityException("Insufficient funds to process transaction");
        }

        // Update account balance
        if ("deposit".equals(request.getType())) {
            account.setBalancePence(account.getBalancePence() + amountPence);
        } else if ("withdrawal".equals(request.getType())) {
            account.setBalancePence(account.getBalancePence() - amountPence);
        }

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setId("tan-" + UUID.randomUUID().toString().substring(0, 8));
        transaction.setAmountPence(amountPence);
        transaction.setCurrency(request.getCurrency());
        transaction.setType(request.getType());
        transaction.setReference(request.getReference());
        transaction.setAccount(account);

        // Save both account and transaction
        accountRepository.save(account);
        transaction = transactionRepository.save(transaction);
        entityManager.flush();
        return mapToTransactionResponse(transaction);
    }

    public List<TransactionResponse> getAccountTransactions(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Bank account was not found"));

        String currentUserId = securityUtils.getCurrentUserId();
        if (!currentUserId.equals(account.getUser().getId())) {
            throw new ForbiddenException("The user is not allowed to access this account");
        }

        List<Transaction> transactions = transactionRepository.findByAccount_AccountNumber(accountNumber);
        return transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse getTransaction(String accountNumber, String transactionId) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Bank account was not found"));

        String currentUserId = securityUtils.getCurrentUserId();
        if (!currentUserId.equals(account.getUser().getId())) {
            throw new ForbiddenException("The user is not allowed to access this account");
        }

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Bank transaction was not found"));

        if (!transaction.getAccount().getAccountNumber().equals(accountNumber)) {
            throw new ForbiddenException("The user is not allowed to access this transaction");
        }

        return mapToTransactionResponse(transaction);
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAmount(BigDecimal.valueOf(transaction.getAmountPence()).divide(BigDecimal.valueOf(100)));
        response.setCurrency(transaction.getCurrency());
        response.setType(transaction.getType());
        response.setReference(transaction.getReference());
        response.setCreatedTimestamp(transaction.getCreatedTimestamp());
        return response;
    }
}
