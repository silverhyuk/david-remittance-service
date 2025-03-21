package com.wirebarley.remittance.domain.transaction;

import com.wirebarley.remittance.common.util.TimeBasedUuidGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 송금 트랜잭션 도메인 엔티티
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {
    private UUID id;
    private UUID sourceAccountId;
    private UUID targetAccountId;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Transaction(UUID id, UUID sourceAccountId, UUID targetAccountId, BigDecimal amount, 
                      TransactionType type, TransactionStatus status, String description) {
        this.id = id != null ? id : TimeBasedUuidGenerator.generate();
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.type = type;
        this.status = status != null ? status : TransactionStatus.PENDING;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 트랜잭션 완료 처리
     * @return 완료된 트랜잭션
     */
    public Transaction complete() {
        if (this.status != TransactionStatus.PENDING) {
            throw new IllegalStateException("대기 상태의 트랜잭션만 완료 처리할 수 있습니다.");
        }
        
        this.status = TransactionStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    /**
     * 트랜잭션 실패 처리
     * @param reason 실패 이유
     * @return 실패한 트랜잭션
     */
    public Transaction fail(String reason) {
        if (this.status != TransactionStatus.PENDING) {
            throw new IllegalStateException("대기 상태의 트랜잭션만 실패 처리할 수 있습니다.");
        }
        
        this.status = TransactionStatus.FAILED;
        this.description = reason;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
}
