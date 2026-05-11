package com.learning.clearing.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountNumber) {
        super("Conta não encontrada: " + accountNumber);
    }
}
