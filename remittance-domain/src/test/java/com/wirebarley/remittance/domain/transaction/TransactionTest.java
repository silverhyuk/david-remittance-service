package com.wirebarley.remittance.domain.transaction;

import com.wirebarley.remittance.common.util.TimeBasedUuidGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    @DisplayName("트랜잭션 생성 테스트")
    void createTransaction() {
        // given
        UUID sourceAccountId = TimeBasedUuidGenerator.generate();
        UUID targetAccountId = TimeBasedUuidGenerator.generate();
        BigDecimal amount = BigDecimal.valueOf(1000);
        String description = "테스트 이체";

        // when
        Transaction transaction = Transaction.builder()
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .description(description)
                .build();

        // then
        assertNotNull(transaction.getId());
        assertEquals(sourceAccountId, transaction.getSourceAccountId());
        assertEquals(targetAccountId, transaction.getTargetAccountId());
        assertEquals(amount, transaction.getAmount());
        assertEquals(TransactionType.TRANSFER, transaction.getType());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertEquals(description, transaction.getDescription());
        assertNotNull(transaction.getCreatedAt());
        assertNotNull(transaction.getUpdatedAt());
    }

    @Test
    @DisplayName("트랜잭션 완료 처리 테스트")
    void completeTransaction() {
        // given
        Transaction transaction = Transaction.builder()
                .sourceAccountId(TimeBasedUuidGenerator.generate())
                .targetAccountId(TimeBasedUuidGenerator.generate())
                .amount(BigDecimal.valueOf(1000))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .description("테스트 이체")
                .build();

        // when
        transaction.complete();

        // then
        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
    }

    @Test
    @DisplayName("트랜잭션 실패 처리 테스트")
    void failTransaction() {
        // given
        Transaction transaction = Transaction.builder()
                .sourceAccountId(TimeBasedUuidGenerator.generate())
                .targetAccountId(TimeBasedUuidGenerator.generate())
                .amount(BigDecimal.valueOf(1000))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .description("테스트 이체")
                .build();
        String failReason = "잔액 부족";

        // when
        transaction.fail(failReason);

        // then
        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
        assertEquals(failReason, transaction.getDescription());
    }

    @Test
    @DisplayName("이미 완료된 트랜잭션 완료 처리 실패 테스트")
    void completeCompletedTransaction() {
        // given
        Transaction transaction = Transaction.builder()
                .sourceAccountId(TimeBasedUuidGenerator.generate())
                .targetAccountId(TimeBasedUuidGenerator.generate())
                .amount(BigDecimal.valueOf(1000))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description("테스트 이체")
                .build();

        // when & then
        assertThrows(IllegalStateException.class, transaction::complete);
    }

    @Test
    @DisplayName("이미 실패한 트랜잭션 완료 처리 실패 테스트")
    void completeFailedTransaction() {
        // given
        Transaction transaction = Transaction.builder()
                .sourceAccountId(TimeBasedUuidGenerator.generate())
                .targetAccountId(TimeBasedUuidGenerator.generate())
                .amount(BigDecimal.valueOf(1000))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.FAILED)
                .description("테스트 이체")
                .build();

        // when & then
        assertThrows(IllegalStateException.class, transaction::complete);
    }
}
