package com.wirebarley.remittance.domain.transaction;

/**
 * 트랜잭션 상태 열거형
 */
public enum TransactionStatus {
    PENDING,    // 대기 중
    COMPLETED,  // 완료됨
    FAILED      // 실패함
}
