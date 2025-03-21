package com.wirebarley.remittance.infrastructure.transaction.entity;

import com.wirebarley.remittance.domain.transaction.Transaction;
import com.wirebarley.remittance.domain.transaction.TransactionStatus;
import com.wirebarley.remittance.domain.transaction.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 트랜잭션 엔티티
 * JPA를 위한 인프라스트럭처 레이어의 엔티티
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionEntity {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "source_account_id", columnDefinition = "BINARY(16)")
    private UUID sourceAccountId;

    @Column(name = "target_account_id", columnDefinition = "BINARY(16)")
    private UUID targetAccountId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 도메인 객체로 변환
     * @return 도메인 트랜잭션 객체
     */
    public Transaction toDomain() {
        return Transaction.builder()
                .id(this.id)
                .sourceAccountId(this.sourceAccountId)
                .targetAccountId(this.targetAccountId)
                .amount(this.amount)
                .type(this.type)
                .status(this.status)
                .description(this.description)
                .build();
    }

    /**
     * 도메인 객체로부터 엔티티 생성
     * @param transaction 도메인 트랜잭션 객체
     * @return 트랜잭션 엔티티
     */
    public static TransactionEntity fromDomain(Transaction transaction) {
        TransactionEntity entity = new TransactionEntity();
        entity.id = transaction.getId();
        entity.sourceAccountId = transaction.getSourceAccountId();
        entity.targetAccountId = transaction.getTargetAccountId();
        entity.amount = transaction.getAmount();
        entity.type = transaction.getType();
        entity.status = transaction.getStatus();
        entity.description = transaction.getDescription();
        entity.createdAt = transaction.getCreatedAt();
        entity.updatedAt = transaction.getUpdatedAt();
        return entity;
    }
}
