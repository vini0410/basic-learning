package com.learning.clearing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.clearing.domain.model.Account;
import com.learning.clearing.domain.model.OutboxEvent;
import com.learning.clearing.domain.model.Transaction;
import com.learning.clearing.domain.repository.AccountRepository;
import com.learning.clearing.domain.repository.OutboxRepository;
import com.learning.clearing.domain.repository.TransactionRepository;
import com.learning.clearing.exception.AccountNotFoundException;
import com.learning.clearing.exception.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private OutboxRepository outboxRepository;
    @Mock
    private AuditService auditService;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RLock lock;

    @InjectMocks
    private SettlementService settlementService;

    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() throws InterruptedException {
        sourceAccount = Account.builder()
                .accountNumber("1234")
                .balance(new BigDecimal("100.00"))
                .build();

        destinationAccount = Account.builder()
                .accountNumber("5678")
                .balance(new BigDecimal("50.00"))
                .build();

        lenient().when(redissonClient.getLock(anyString())).thenReturn(lock);
        lenient().when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
    }

    @Test
    void transfer_Success() throws Exception {
        when(accountRepository.findByAccountNumber("1234")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("5678")).thenReturn(Optional.of(destinationAccount));
        
        Transaction mockTransaction = Transaction.builder()
                .id(java.util.UUID.randomUUID())
                .sourceAccountNumber("1234")
                .destinationAccountNumber("5678")
                .amount(new BigDecimal("30.00"))
                .status("COMPLETED")
                .transactionDate(java.time.LocalDateTime.now())
                .build();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        Transaction result = settlementService.transfer("1234", "5678", new BigDecimal("30.00"));

        assertNotNull(result);
        assertEquals(new BigDecimal("70.00"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("80.00"), destinationAccount.getBalance());
        verify(outboxRepository, times(1)).save(any(OutboxEvent.class));
    }

    @Test
    void transfer_InsufficientBalance_ThrowsException() {
        when(accountRepository.findByAccountNumber("1234")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("5678")).thenReturn(Optional.of(destinationAccount));

        assertThrows(InsufficientBalanceException.class, () -> 
            settlementService.transfer("1234", "5678", new BigDecimal("150.00"))
        );

        verify(outboxRepository, never()).save(any(OutboxEvent.class));
    }

    @Test
    void transfer_AccountNotFound_ThrowsException() {
        when(accountRepository.findByAccountNumber("1234")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> 
            settlementService.transfer("1234", "5678", new BigDecimal("10.00"))
        );
        
        verify(outboxRepository, never()).save(any(OutboxEvent.class));
    }
}
