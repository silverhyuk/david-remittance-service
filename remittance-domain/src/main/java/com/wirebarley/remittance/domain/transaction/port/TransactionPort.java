package com.wirebarley.remittance.domain.transaction.port;

import com.wirebarley.remittance.domain.transaction.Transaction;
import com.wirebarley.remittance.domain.transaction.TransactionStatus;
import com.wirebarley.remittance.domain.transaction.TransactionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 트랜잭션 리포지토리 포트 인터페이스
 * 헥사고날 아키텍처의 포트(Port) - 도메인에서 외부로 향하는 인터페이스
 */
public interface TransactionPort {
    /**
     * 트랜잭션 저장
     * @param transaction 저장할 트랜잭션
     * @return 저장된 트랜잭션
     */
    Transaction save(Transaction transaction);
    
    /**
     * ID로 트랜잭션 조회
     * @param id 트랜잭션 ID
     * @return 트랜잭션 Optional
     */
    Optional<Transaction> findById(UUID id);
    
    /**
     * 계좌 ID로 트랜잭션 조회
     * @param accountId 계좌 ID
     * @return 트랜잭션 목록
     */
    List<Transaction> findBySourceAccountId(UUID accountId);
    
    /**
     * 대상 계좌 ID로 트랜잭션 조회
     * @param accountId 계좌 ID
     * @return 트랜잭션 목록
     */
    List<Transaction> findByTargetAccountId(UUID accountId);
    
    /**
     * 트랜잭션 타입으로 트랜잭션 조회
     * @param type 트랜잭션 타입
     * @return 트랜잭션 목록
     */
    List<Transaction> findByType(TransactionType type);
    
    /**
     * 트랜잭션 상태로 트랜잭션 조회
     * @param status 트랜잭션 상태
     * @return 트랜잭션 목록
     */
    List<Transaction> findByStatus(TransactionStatus status);
}
