package com.learning.clearing.messaging;

import com.learning.clearing.domain.event.SettlementEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementConsumer {

    private final RedissonClient redissonClient;

    @KafkaListener(topics = "clearing-settlements", groupId = "clearing-group")
    public void consumeSettlementEvent(SettlementEvent event) {
        String transactionId = event.getTransactionId();
        String redisKey = "processed:settlement:" + transactionId;
        String lockKey = "lock:consumer:settlement:" + transactionId;

        RBucket<Boolean> bucket = redissonClient.getBucket(redisKey);
        
        if (Boolean.TRUE.equals(bucket.get())) {
            log.info("Evento de liquidação duplicado detectado para transação ID: {}. Ignorando.", transactionId);
            return;
        }

        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                try {
                    // Dupla checagem após o lock
                    if (Boolean.TRUE.equals(bucket.get())) {
                        return;
                    }

                    log.info("Processando evento de liquidação do Kafka: ID={} | Origem={} | Destino={} | Valor={}",
                            transactionId,
                            event.getSourceAccountNumber(),
                            event.getDestinationAccountNumber(),
                            event.getAmount());
                    
                    // Lógica de processamento (ex: notificação, dashboard, analytics)
                    // ... simulate work ...

                    // Marca como processado com TTL de 7 dias para evitar crescimento infinito do Redis
                    bucket.set(true, 7, TimeUnit.DAYS);

                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Erro de concorrência ao processar evento de liquidação: {}", e.getMessage());
        }
    }
}
