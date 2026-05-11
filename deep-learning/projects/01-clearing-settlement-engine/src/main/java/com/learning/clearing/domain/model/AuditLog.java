package com.learning.clearing.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType; // EX: TRANSFER_START, TRANSFER_SUCCESS, TRANSFER_FAILED

    @Column(nullable = false)
    private String entityId; // Ex: Número da conta ou ID da transação

    @Column(columnDefinition = "TEXT")
    private String details;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    private String source; // Ex: SettlementService
}
