package com.wirebarley.remittance.application.transaction.usecase;

import com.wirebarley.remittance.application.transaction.read.TransactionRead;

import java.util.List;
import java.util.UUID;

/**
 * 트랜잭션 Query UseCase
 */
public interface TransactionQueryUseCase {
    /**
     * 트랜잭션 조회
     * @param transactionId 트랜잭션 ID
     * @return 트랜잭션 DTO
     */
    TransactionRead getTransaction(UUID transactionId);
    
    /**
     * 계좌 ID로 출금 트랜잭션 조회
     * @param accountId 계좌 ID
     * @return 트랜잭션 DTO 목록
     */
    List<TransactionRead> getTransactionsBySourceAccount(UUID accountId);
    
    /**
     * 계좌 ID로 입금 트랜잭션 조회
     * @param accountId 계좌 ID
     * @return 트랜잭션 DTO 목록
     */
    List<TransactionRead> getTransactionsByTargetAccount(UUID accountId);
}
