package com.learning.clearing.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String accountNumber, BigDecimal balance, BigDecimal requested) {
        super(String.format("Saldo insuficiente na conta %s. Saldo atual: %s, Valor solicitado: %s", 
                accountNumber, balance, requested));
    }
}
