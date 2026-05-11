package com.learning.clearing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.clearing.domain.event.SettlementEvent;
import com.learning.clearing.domain.model.OutboxEvent;
import com.learning.clearing.domain.repository.OutboxRepository;
import com.learning.clearing.messaging.SettlementProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxRelay {

    private final OutboxRepository outboxRepository;
    private final com.learning.clearing.domain.repository.OutboxDeadLetterRepository dlqRepository;
    private final SettlementProducer settlementProducer;
    private final ObjectMapper objectMapper;

    @org.springframework.beans.factory.annotation.Value("${app.mock-outbox-error:false}")
    private boolean mockError;

    private static final int MAX_RETRY_COUNT = 5;

    @Scheduled(fixedDelay = 5000) // Executa a cada 5 segundos
    @Transactional
    public void processOutboxEvents() {
        log.debug("Verificando eventos na tabela Outbox... (Mock Error: {})", mockError);

        List<OutboxEvent> events = outboxRepository.findByProcessedFalseOrderByCreatedAtAsc();

        if (events.isEmpty()) {
            return;
        }

        log.info("Processando {} eventos pendentes na Outbox", events.size());

        for (OutboxEvent event : events) {
            try {
                if (mockError) {
                    throw new RuntimeException("ERRO SIMULADO: Variável MOCK_OUTBOX_ERROR está ativa.");
                }

                // 1. Desserializa o payload de volta para SettlementEvent
                SettlementEvent settlementEvent = objectMapper.readValue(event.getPayload(), SettlementEvent.class);

                // 2. Tenta enviar para o Kafka
                settlementProducer.sendSettlementEvent(settlementEvent);

                // 3. Marca como processado
                event.setProcessed(true);
                event.setProcessedAt(LocalDateTime.now());
                int currentRetry = (event.getRetryCount() != null ? event.getRetryCount() : 0) + 1;
                event.setRetryCount(currentRetry);
                outboxRepository.save(event);

                log.info("Evento Outbox {} processado com sucesso", event.getId());

            } catch (Exception e) {
                log.error("Erro ao processar evento Outbox {}: {}.", event.getId(), e.getMessage());
                
                int currentRetry = (event.getRetryCount() != null ? event.getRetryCount() : 0) + 1;
                event.setRetryCount(currentRetry);
                event.setErrorMessage(e.getMessage());

                if (currentRetry >= MAX_RETRY_COUNT) {
                    log.error("Evento Outbox {} excedeu {} tentativas. Movendo para a Dead Letter Table.", event.getId(), MAX_RETRY_COUNT);
                    
                    dlqRepository.save(com.learning.clearing.domain.model.OutboxDeadLetter.builder()
                            .eventType(event.getEventType())
                            .payload(event.getPayload())
                            .createdAt(event.getCreatedAt())
                            .movedToDlqAt(LocalDateTime.now())
                            .lastErrorMessage(e.getMessage())
                            .build());
                    
                    // Remove do Outbox original para parar de tentar
                    outboxRepository.delete(event);
                } else {
                    outboxRepository.save(event);
                    log.info("Evento {} marcado para retentativa (Tentativa {}/{})", event.getId(), currentRetry, MAX_RETRY_COUNT);
                }
            }
        }
    }
}
