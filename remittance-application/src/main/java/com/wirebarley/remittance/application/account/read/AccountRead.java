package com.wirebarley.remittance.application.account.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 계좌 조회 결과 모델
 * CQRS 패턴의 Query 모델
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRead {
    private UUID id;
    private String accountNumber;
    private String accountName;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
