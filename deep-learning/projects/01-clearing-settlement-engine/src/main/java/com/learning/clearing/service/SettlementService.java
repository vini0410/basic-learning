package com.learning.clearing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.clearing.domain.event.SettlementEvent;
import com.learning.clearing.domain.model.Account;
import com.learning.clearing.domain.model.OutboxEvent;
import com.learning.clearing.domain.model.Transaction;
import com.learning.clearing.domain.repository.AccountRepository;
import com.learning.clearing.domain.repository.OutboxRepository;
import com.learning.clearing.domain.repository.TransactionRepository;
import com.learning.clearing.exception.AccountNotFoundException;
import com.learning.clearing.exception.InsufficientBalanceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final OutboxRepository outboxRepository;
    private final AuditService auditService;
    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public Transaction transfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) {
        log.info("Iniciando transferência de {} de {} para {}", amount, sourceAccountNumber, destinationAccountNumber);

        // Registro de auditoria inicial
        auditService.log("TRANSFER_REQUESTED", sourceAccountNumber, "SettlementService", 
                String.format("Transferência de %s para %s", amount, destinationAccountNumber));

        String firstLockKey = sourceAccountNumber.compareTo(destinationAccountNumber) < 0 ? sourceAccountNumber : destinationAccountNumber;
        String secondLockKey = firstLockKey.equals(sourceAccountNumber) ? destinationAccountNumber : sourceAccountNumber;

        RLock lock1 = redissonClient.getLock("lock:account:" + firstLockKey);
        RLock lock2 = redissonClient.getLock("lock:account:" + secondLockKey);

        try {
            if (lock1.tryLock(10, 30, TimeUnit.SECONDS) && lock2.tryLock(10, 30, TimeUnit.SECONDS)) {
                try {
                    Account sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber)
                            .orElseThrow(() -> {
                                auditService.log("TRANSFER_FAILED", sourceAccountNumber, "SettlementService", "Conta de origem não encontrada");
                                return new AccountNotFoundException(sourceAccountNumber);
                            });

                    Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber)
                            .orElseThrow(() -> {
                                auditService.log("TRANSFER_FAILED", destinationAccountNumber, "SettlementService", "Conta de destino não encontrada");
                                return new AccountNotFoundException(destinationAccountNumber);
                            });

                    if (sourceAccount.getBalance().compareTo(amount) < 0) {
                        auditService.log("TRANSFER_FAILED", sourceAccountNumber, "SettlementService", 
                                String.format("Saldo insuficiente: %s, necessário: %s", sourceAccount.getBalance(), amount));
                        throw new InsufficientBalanceException(sourceAccountNumber, sourceAccount.getBalance(), amount);
                    }

                    sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
                    destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

                    accountRepository.save(sourceAccount);
                    accountRepository.save(destinationAccount);

                    Transaction transaction = transactionRepository.save(Transaction.builder()
                            .sourceAccountNumber(sourceAccountNumber)
                            .destinationAccountNumber(destinationAccountNumber)
                            .amount(amount)
                            .transactionDate(LocalDateTime.now())
                            .status("COMPLETED")
                            .description("Transferência realizada com sucesso")
                            .build());

                    // Registro de auditoria de sucesso
                    auditService.log("TRANSFER_COMPLETED", transaction.getId().toString(), "SettlementService", 
                            String.format("Transferência finalizada: %s de %s para %s", amount, sourceAccountNumber, destinationAccountNumber));

                    // Salva na tabela Outbox (em vez de enviar direto ao Kafka)
                    SettlementEvent event = SettlementEvent.builder()
                            .transactionId(transaction.getId().toString())
                            .sourceAccountNumber(sourceAccountNumber)
                            .destinationAccountNumber(destinationAccountNumber)
                            .amount(amount)
                            .timestamp(transaction.getTransactionDate())
                            .status("COMPLETED")
                            .build();

                    outboxRepository.save(OutboxEvent.builder()
                            .eventType("SETTLEMENT_COMPLETED")
                            .payload(objectMapper.writeValueAsString(event))
                            .createdAt(LocalDateTime.now())
                            .processed(false)
                            .build());

                    return transaction;
                } finally {
                    lock2.unlock();
                    lock1.unlock();
                }
            } else {
                auditService.log("TRANSFER_FAILED", sourceAccountNumber, "SettlementService", "Não foi possível adquirir o lock");
                throw new RuntimeException("Não foi possível adquirir o lock para realizar a transferência.");
            }
        } catch (AccountNotFoundException | InsufficientBalanceException e) {
            log.error("Erro de negócio na transferência: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro no processamento da transferência", e);
            auditService.log("TRANSFER_ERROR", sourceAccountNumber, "SettlementService", e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Falha na liquidação: " + e.getMessage());
        }
    }
}
