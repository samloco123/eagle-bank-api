package com.eaglebank.service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglebank.dto.AccountRequest;
import com.eaglebank.dto.AccountResponse;
import com.eaglebank.exception.ForbiddenException;
import com.eaglebank.exception.NotFoundException;
import com.eaglebank.model.Account;
import com.eaglebank.model.User;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.UserRepository;
import com.eaglebank.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;


    public AccountResponse createAccount(AccountRequest request) {
        String userId = securityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setSortCode("10-10-10");
        account.setName(request.getName());
        account.setAccountType(request.getAccountType());
        account.setBalancePence(0L); // Start with zero balance
        account.setCurrency("GBP");
        account.setUser(user);

        account = accountRepository.save(account);
        return mapToAccountResponse(account);
    }

    public List<AccountResponse> getUserAccounts() {
        String userId = securityUtils.getCurrentUserId();
        List<Account> accounts = accountRepository.findByUser_Id(userId);
        return accounts.stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Bank account was not found"));

        String currentUserId = securityUtils.getCurrentUserId();
        if (!currentUserId.equals(account.getUser().getId())) {
            throw new ForbiddenException("The user is not allowed to access this account");
        }
        return mapToAccountResponse(account);
    }

    @Transactional
    public AccountResponse updateAccount(String accountNumber, AccountRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Bank account was not found"));

        String currentUserId = securityUtils.getCurrentUserId();
        if (!currentUserId.equals(account.getUser().getId())) {
            throw new ForbiddenException("The user is not allowed to access this account");
        }

        if (request.getName() != null) {
            account.setName(request.getName());
        }
        if (request.getAccountType() != null) {
            account.setAccountType(request.getAccountType());
        }

        account = accountRepository.save(account);
        return mapToAccountResponse(account);
    }

    @Transactional
    public void deleteAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Bank account was not found"));

        String currentUserId = securityUtils.getCurrentUserId();
        if (!currentUserId.equals(account.getUser().getId())) {
            throw new ForbiddenException("The user is not allowed to access this account");
        }
        
        accountRepository.delete(account);
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("01%06d", SECURE_RANDOM.nextInt(1_000_000));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private AccountResponse mapToAccountResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setAccountNumber(account.getAccountNumber());
        response.setSortCode(account.getSortCode());
        response.setName(account.getName());
        response.setAccountType(account.getAccountType());
        response.setBalance(BigDecimal.valueOf(account.getBalancePence()).divide(BigDecimal.valueOf(100)));
        response.setCurrency(account.getCurrency());
        response.setCreatedTimestamp(account.getCreatedTimestamp());
        response.setUpdatedTimestamp(account.getUpdatedTimestamp());
        return response;
    }
}
