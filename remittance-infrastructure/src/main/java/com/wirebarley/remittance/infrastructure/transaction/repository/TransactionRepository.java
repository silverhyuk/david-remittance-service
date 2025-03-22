package com.wirebarley.remittance.infrastructure.transaction.repository;

import com.wirebarley.remittance.domain.transaction.TransactionStatus;
import com.wirebarley.remittance.domain.transaction.TransactionType;
import com.wirebarley.remittance.infrastructure.transaction.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA 트랜잭션 리포지토리
 */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    /**
     * 출금 계좌 ID로 트랜잭션 조회
     * @param sourceAccountId 출금 계좌 ID
     * @return 트랜잭션 목록
     */
    List<TransactionEntity> findBySourceAccountId(UUID sourceAccountId);
    
    /**
     * 입금 계좌 ID로 트랜잭션 조회
     * @param targetAccountId 입금 계좌 ID
     * @return 트랜잭션 목록
     */
    List<TransactionEntity> findByTargetAccountId(UUID targetAccountId);
    
    /**
     * 트랜잭션 타입으로 트랜잭션 조회
     * @param type 트랜잭션 타입
     * @return 트랜잭션 목록
     */
    List<TransactionEntity> findByType(TransactionType type);
    
    /**
     * 트랜잭션 상태로 트랜잭션 조회
     * @param status 트랜잭션 상태
     * @return 트랜잭션 목록
     */
    List<TransactionEntity> findByStatus(TransactionStatus status);
}
