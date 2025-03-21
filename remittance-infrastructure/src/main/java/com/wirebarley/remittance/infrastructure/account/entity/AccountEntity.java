package com.wirebarley.remittance.infrastructure.account.entity;

import com.wirebarley.remittance.domain.account.Account;
import com.wirebarley.remittance.domain.account.AccountStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 계좌 엔티티
 * JPA를 위한 인프라스트럭처 레이어의 엔티티
 */
@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountEntity {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 도메인 객체로 변환
     * @return 도메인 계좌 객체
     */
    public Account toDomain() {
        return Account.builder()
                .id(this.id)
                .accountNumber(this.accountNumber)
                .accountName(this.accountName)
                .balance(this.balance)
                .status(this.status)
                .build();
    }

    /**
     * 도메인 객체로부터 엔티티 생성
     * @param account 도메인 계좌 객체
     * @return 계좌 엔티티
     */
    public static AccountEntity fromDomain(Account account) {
        AccountEntity entity = new AccountEntity();
        entity.id = account.getId();
        entity.accountNumber = account.getAccountNumber();
        entity.accountName = account.getAccountName();
        entity.balance = account.getBalance();
        entity.status = account.getStatus();
        entity.createdAt = account.getCreatedAt();
        entity.updatedAt = account.getUpdatedAt();
        return entity;
    }
}
