package com.eaglebank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaglebank.dto.AccountRequest;
import com.eaglebank.dto.AccountResponse;
import com.eaglebank.exception.ForbiddenException;
import com.eaglebank.exception.NotFoundException;
import com.eaglebank.model.Account;
import com.eaglebank.model.User;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.UserRepository;
import com.eaglebank.util.SecurityUtils;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private AccountService accountService;

    private User userWithId(String id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private Account accountOwnedBy(String accountNumber, User owner) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setSortCode("10-10-10");
        account.setUser(owner);
        return account;
    }

    @Test
    void createAccount_Success() {
        AccountRequest request = new AccountRequest();
        request.setName("Current Account");
        request.setAccountType("personal");

        User user = userWithId("usr-abc123");
        when(securityUtils.getCurrentUserId()).thenReturn("usr-abc123");
        when(userRepository.findById("usr-abc123")).thenReturn(Optional.of(user));
        when(accountRepository.existsByAccountNumber(any())).thenReturn(false);
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AccountResponse result = accountService.createAccount(request);

        assertNotNull(result);
        assertEquals("Current Account", result.getName());
        assertEquals("10-10-10", result.getSortCode());
        verify(accountRepository, times(1)).save(any());
    }

    @Test
    void createAccount_UserNotFound_ThrowsNotFoundException() {
        when(securityUtils.getCurrentUserId()).thenReturn("usr-missing");
        when(userRepository.findById("usr-missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.createAccount(new AccountRequest()));
    }

    @Test
    void getAccount_Success() {
        User user = userWithId("usr-abc123");
        Account account = accountOwnedBy("01123456", user);

        when(securityUtils.getCurrentUserId()).thenReturn("usr-abc123");
        when(accountRepository.findByAccountNumber("01123456")).thenReturn(Optional.of(account));

        AccountResponse result = accountService.getAccount("01123456");

        assertNotNull(result);
        assertEquals("01123456", result.getAccountNumber());
    }

    @Test
    void getAccount_NotFound_ThrowsNotFoundException() {
        when(accountRepository.findByAccountNumber("01999999")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.getAccount("01999999"));
    }

    @Test
    void getAccount_OtherUsersAccount_ThrowsForbiddenException() {
        User owner = userWithId("usr-owner");
        Account account = accountOwnedBy("01123456", owner);

        when(securityUtils.getCurrentUserId()).thenReturn("usr-other");
        when(accountRepository.findByAccountNumber("01123456")).thenReturn(Optional.of(account));

        assertThrows(ForbiddenException.class, () -> accountService.getAccount("01123456"));
    }

    @Test
    void updateAccount_Success() {
        User user = userWithId("usr-abc123");
        Account account = accountOwnedBy("01123456", user);
        account.setName("Old Name");

        AccountRequest request = new AccountRequest();
        request.setName("New Name");

        when(securityUtils.getCurrentUserId()).thenReturn("usr-abc123");
        when(accountRepository.findByAccountNumber("01123456")).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AccountResponse result = accountService.updateAccount("01123456", request);

        assertEquals("New Name", result.getName());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void deleteAccount_OtherUsersAccount_ThrowsForbiddenException() {
        User owner = userWithId("usr-owner");
        Account account = accountOwnedBy("01123456", owner);

        when(securityUtils.getCurrentUserId()).thenReturn("usr-other");
        when(accountRepository.findByAccountNumber("01123456")).thenReturn(Optional.of(account));

        assertThrows(ForbiddenException.class, () -> accountService.deleteAccount("01123456"));
        verify(accountRepository, never()).delete(any());
    }
}
