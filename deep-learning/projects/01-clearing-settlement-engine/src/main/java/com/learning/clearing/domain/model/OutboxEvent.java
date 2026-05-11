package com.learning.clearing.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType; // Ex: SETTLEMENT_COMPLETED

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload; // O evento serializado em JSON

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    @Column(nullable = false)
    private boolean processed = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer retryCount = 0;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}
