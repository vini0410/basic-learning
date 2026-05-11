package com.learning.clearing.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(TransactionId.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @Id
    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column(nullable = false)
    private String sourceAccountNumber;

    @Column(nullable = false)
    private String destinationAccountNumber;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status; // Ex: COMPLETED, FAILED, PENDING

    private String description;
}
