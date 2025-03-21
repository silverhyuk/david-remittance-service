package com.wirebarley.remittance.application.transaction.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 트랜잭션 조회 결과 모델
 * CQRS 패턴의 Query 모델
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRead {
    private UUID id;
    private UUID sourceAccountId;
    private String sourceAccountNumber;
    private UUID targetAccountId;
    private String targetAccountNumber;
    private BigDecimal amount;
    private String type;
    private String status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
