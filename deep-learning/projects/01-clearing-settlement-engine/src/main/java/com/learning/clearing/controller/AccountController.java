package com.learning.clearing.controller;

import com.learning.clearing.domain.model.Account;
import com.learning.clearing.domain.model.AuditLog;
import com.learning.clearing.domain.model.Transaction;
import com.learning.clearing.domain.repository.AccountRepository;
import com.learning.clearing.domain.repository.AuditRepository;
import com.learning.clearing.service.SettlementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountRepository accountRepository;
    private final SettlementService settlementService;
    private final AuditRepository auditRepository;
    private final RedissonClient redissonClient;

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/{accountNumber}/audit")
    public List<AuditLog> getAuditLogs(@PathVariable String accountNumber) {
        return auditRepository.findByEntityIdOrderByTimestampDesc(accountNumber);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccountByNumber(@PathVariable String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(
            @Valid @RequestBody TransferRequest request,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {
        
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            Transaction transaction = settlementService.transfer(
                    request.getSourceAccount(),
                    request.getDestinationAccount(),
                    request.getAmount()
            );
            return ResponseEntity.ok(transaction);
        }

        String redisKey = "idempotency:transfer:" + idempotencyKey;
        RBucket<Transaction> bucket = redissonClient.getBucket(redisKey);
        
        Transaction cachedTransaction = bucket.get();
        if (cachedTransaction != null) {
            log.info("Requisição duplicada detectada para chave de idempotência: {}. Retornando resultado do cache.", idempotencyKey);
            return ResponseEntity.ok(cachedTransaction);
        }

        RLock lock = redissonClient.getLock("lock:idempotency:" + idempotencyKey);
        try {
            if (lock.tryLock(5, 15, TimeUnit.SECONDS)) {
                try {
                    // Dupla checagem após adquirir o lock
                    cachedTransaction = bucket.get();
                    if (cachedTransaction != null) {
                        return ResponseEntity.ok(cachedTransaction);
                    }

                    Transaction transaction = settlementService.transfer(
                            request.getSourceAccount(),
                            request.getDestinationAccount(),
                            request.getAmount()
                    );
                    
                    // Salva no cache por 24 horas
                    bucket.set(transaction, 24, TimeUnit.HOURS);
                    return ResponseEntity.ok(transaction);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("Não foi possível processar a requisição devido a alta concorrência na mesma chave de idempotência.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrompida durante o processamento da idempotência.");
        }
    }

    @Data
    public static class TransferRequest {
        @NotBlank(message = "Conta de origem é obrigatória")
        private String sourceAccount;

        @NotBlank(message = "Conta de destino é obrigatória")
        private String destinationAccount;

        @NotNull(message = "Valor da transferência é obrigatório")
        @Positive(message = "Valor da transferência deve ser positivo")
        private BigDecimal amount;
    }
}
