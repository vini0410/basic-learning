package com.learning.clearing.domain.model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TransactionId implements Serializable {
    private UUID id;
    private LocalDateTime transactionDate;
}
