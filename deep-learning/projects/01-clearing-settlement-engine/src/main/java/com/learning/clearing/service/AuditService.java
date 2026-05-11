package com.learning.clearing.service;

import com.learning.clearing.domain.model.AuditLog;
import com.learning.clearing.domain.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    /**
     * Registra um evento de auditoria de forma assíncrona.
     * O uso de @Async evita o deadlock no pool de conexões, pois o log será salvo
     * em uma transação própria em uma thread separada do fluxo principal.
     */
    @Async
    @Transactional
    public void log(String eventType, String entityId, String source, String details) {
        log.info("Auditoria: {} | Entity: {} | Source: {} | Details: {}", eventType, entityId, source, details);
        
        AuditLog auditLog = AuditLog.builder()
                .eventType(eventType)
                .entityId(entityId)
                .source(source)
                .details(details)
                .build();
        
        auditRepository.save(auditLog);
    }
}
