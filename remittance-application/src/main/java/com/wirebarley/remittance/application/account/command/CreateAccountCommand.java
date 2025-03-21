package com.wirebarley.remittance.application.account.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 계좌 생성 커맨드
 * CQRS 패턴의 Command 모델
 */
@Getter
@Builder
public class CreateAccountCommand {
    private final String accountNumber;
    private final String accountName;
    private final BigDecimal initialBalance;
}
