package com.wirebarley.remittance.application.transaction.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 이체 커맨드
 * CQRS 패턴의 Command 모델
 */
@Getter
@Builder
public class TransferCommand {
    private final UUID sourceAccountId;
    private final UUID targetAccountId;
    private final BigDecimal amount;
    private final String description;
}
