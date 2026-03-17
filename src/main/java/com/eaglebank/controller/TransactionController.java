package com.eaglebank.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eaglebank.dto.TransactionRequest;
import com.eaglebank.dto.TransactionResponse;
import com.eaglebank.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/accounts/{accountNumber}/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@PathVariable String accountNumber, 
                                                @Valid @RequestBody TransactionRequest request) {
        return transactionService.createTransaction(accountNumber, request);
    }

    @GetMapping
    public List<TransactionResponse> getAccountTransactions(@PathVariable String accountNumber) {
        return transactionService.getAccountTransactions(accountNumber);
    }

    @GetMapping("/{transactionId}")
    public TransactionResponse getTransaction(@PathVariable String accountNumber, 
                                            @PathVariable String transactionId) {
        return transactionService.getTransaction(accountNumber, transactionId);
    }
}
