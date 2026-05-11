package com.learning.clearing.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_dead_letter")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxDeadLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime movedToDlqAt;

    @Column(columnDefinition = "TEXT")
    private String lastErrorMessage;
}
