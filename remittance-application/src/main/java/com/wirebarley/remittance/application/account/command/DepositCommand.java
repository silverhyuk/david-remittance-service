package com.wirebarley.remittance.application.account.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 입금 커맨드
 * CQRS 패턴의 Command 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositCommand {
    private UUID accountId;
    private BigDecimal amount;
    private String description;
}
