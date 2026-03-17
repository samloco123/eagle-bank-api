package com.eaglebank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaglebank.dto.TransactionRequest;
import com.eaglebank.dto.TransactionResponse;
import com.eaglebank.exception.ForbiddenException;
import com.eaglebank.exception.NotFoundException;
import com.eaglebank.exception.UnprocessableEntityException;
import com.eaglebank.model.Account;
import com.eaglebank.model.User;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.TransactionRepository;
import com.eaglebank.util.SecurityUtils;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TransactionService transactionService;

    private Account accountOwnedBy(String userId, long balancePence) {
        User user = new User();
        user.setId(userId);
        Account account = new Account();
        account.setAccountNumber("01123456");
        account.setBalancePence(balancePence);
        account.setUser(user);
        return account;
    }

    @Test
    void createTransaction_Deposit_UpdatesBalanceAndReturnsResponse() {
        Account account = accountOwnedBy("usr-test123", 5000L); // £50.00

        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("25.00"));
        request.setCurrency("GBP");
        request.setType("deposit");

        when(securityUtils.getCurrentUserId()).thenReturn("usr-test123");
        when(accountRepository.findByAccountNumber("01123456")).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(entityManager).flush();

        TransactionResponse result = transactionService.createTransaction("01123456", request);

        assertNotNull(result);
        assertEquals("deposit", result.getType());
        assertEquals(0, new BigDecimal("25.00").compareTo(result.getAmount()));
        assertEquals(7500L, account.getBalancePence()); // balance updated to £75.00
    }

    @Test
    void createTransaction_Withdrawal_DeductsBalanceCorrectly() {
        Account account = accountOwnedBy("usr-test123", 10000L); // £100.00

        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("40.00"));
        request.setCurrency("GBP");
        request.setType("withdrawal");

        when(securityUtils.getCurrentUserId()).thenReturn("usr-test123");
        when(accountRepository.findByAccountNumber("01123456")).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(entityManager).flush();

        transactionService.createTransaction("01123456", request);

        assertEquals(6000L, account.getBalancePence()); // balance reduced to £60.00
    }

    @Test
    void createTransaction_InsufficientFunds_ThrowsUnprocessableEntityException() {
        Account account = accountOwnedBy("usr-test123", 5000L); // £50.00

        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("GBP");
        request.setType("withdrawal");

        when(securityUtils.getCurrentUserId()).thenReturn("usr-test123");
        when(accountRepository.findByAccountNumber("01123456")).thenReturn(Optional.of(account));

        assertThrows(UnprocessableEntityException.class,
                () -> transactionService.createTransaction("01123456", request));
    }

    @Test
    void createTransaction_AccountNotFound_ThrowsNotFoundException() {
        when(accountRepository.findByAccountNumber("01999999")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> transactionService.createTransaction("01999999", new TransactionRequest()));
    }

    @Test
    void createTransaction_OtherUsersAccount_ThrowsForbiddenException() {
        Account account = accountOwnedBy("usr-owner", 10000L);

        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("10.00"));
        request.setType("deposit");

        when(securityUtils.getCurrentUserId()).thenReturn("usr-other");
        when(accountRepository.findByAccountNumber("01123456")).thenReturn(Optional.of(account));

        assertThrows(ForbiddenException.class,
                () -> transactionService.createTransaction("01123456", request));
    }

    @Test
    void getAccountTransactions_OtherUsersAccount_ThrowsForbiddenException() {
        Account account = accountOwnedBy("usr-owner", 10000L);

        when(securityUtils.getCurrentUserId()).thenReturn("usr-other");
        when(accountRepository.findByAccountNumber("01123456")).thenReturn(Optional.of(account));

        assertThrows(ForbiddenException.class,
                () -> transactionService.getAccountTransactions("01123456"));
    }
}
