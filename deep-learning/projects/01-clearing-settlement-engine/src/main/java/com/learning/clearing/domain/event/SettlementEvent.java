package com.learning.clearing.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementEvent {
    private String transactionId;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String status;
}
