package com.wirebarley.remittance.domain.account;

import com.wirebarley.remittance.common.util.TimeBasedUuidGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 계좌 도메인 엔티티
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    private UUID id;
    private String accountNumber;
    private String accountName;
    private BigDecimal balance;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Account(UUID id, String accountNumber, String accountName, BigDecimal balance, AccountStatus status) {
        this.id = id != null ? id : TimeBasedUuidGenerator.generate();
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.status = status != null ? status : AccountStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 입금 처리
     * @param amount 입금액
     * @return 입금 후 계좌
     */
    public Account deposit(BigDecimal amount) {
        validateAccountStatus();
        validateAmount(amount);
        
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    /**
     * 출금 처리
     * @param amount 출금액
     * @return 출금 후 계좌
     */
    public Account withdraw(BigDecimal amount) {
        validateAccountStatus();
        validateAmount(amount);
        validateBalance(amount);
        
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    /**
     * 계좌 상태 검증
     */
    private void validateAccountStatus() {
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("계좌가 활성 상태가 아닙니다.");
        }
    }

    /**
     * 금액 검증
     * @param amount 검증할 금액
     */
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 합니다.");
        }
    }

    /**
     * 잔액 검증
     * @param amount 출금할 금액
     */
    private void validateBalance(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }
    }
}
