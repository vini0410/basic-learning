package com.learning.clearing.messaging;

import com.learning.clearing.domain.event.SettlementEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementProducer {

    private final KafkaTemplate<String, SettlementEvent> kafkaTemplate;
    private static final String TOPIC = "clearing-settlements";

    @Retry(name = "kafkaProducer")
    @CircuitBreaker(name = "kafkaProducer")
    public void sendSettlementEvent(SettlementEvent event) {
        log.info("Publicando evento de liquidação no Kafka: {}", event.getTransactionId());
        try {
            kafkaTemplate.send(TOPIC, event.getTransactionId(), event).get(5, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Falha ao enviar evento para o Kafka: {}", e.getMessage());
            throw new RuntimeException("Erro de integração com Kafka", e);
        }
    }
}
