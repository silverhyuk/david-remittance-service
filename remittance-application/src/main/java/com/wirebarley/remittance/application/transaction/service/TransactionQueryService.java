package com.wirebarley.remittance.application.transaction.service;

import com.wirebarley.remittance.application.transaction.read.TransactionRead;
import com.wirebarley.remittance.application.transaction.usecase.TransactionQueryUseCase;
import com.wirebarley.remittance.domain.account.Account;
import com.wirebarley.remittance.domain.account.port.AccountPort;
import com.wirebarley.remittance.domain.transaction.port.TransactionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 트랜잭션 Query 서비스
 */
@Service
@RequiredArgsConstructor
public class TransactionQueryService implements TransactionQueryUseCase {
    private final TransactionPort transactionPort;  // 도메인 포트를 의존성 주입받음
    private final AccountPort accountPort;          // 도메인 포트를 의존성 주입받음

    /**
     * 트랜잭션 조회 
     * @param transactionId 트랜잭션 ID
     * @return 트랜잭션 DTO
     */
    @Transactional(readOnly = true)
    public TransactionRead getTransaction(UUID transactionId) {
        // 조회 작업만 수행 (read-only operations)
        com.wirebarley.remittance.domain.transaction.Transaction transaction = transactionPort.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("트랜잭션을 찾을 수 없습니다: " + transactionId));
        
        return mapToDto(transaction);
    }
    
    /**
     * 계좌 ID로 출금 트랜잭션 조회 
     * @param accountId 계좌 ID
     * @return 트랜잭션 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<TransactionRead> getTransactionsBySourceAccount(UUID accountId) {
        return transactionPort.findBySourceAccountId(accountId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 계좌 ID로 입금 트랜잭션 조회 
     * @param accountId 계좌 ID
     * @return 트랜잭션 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<TransactionRead> getTransactionsByTargetAccount(UUID accountId) {
        return transactionPort.findByTargetAccountId(accountId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 도메인 객체를 DTO로 변환
     * @param transaction 도메인 트랜잭션 객체
     * @return 트랜잭션 DTO
     */
    private TransactionRead mapToDto(com.wirebarley.remittance.domain.transaction.Transaction transaction) {
        // 도메인 로직을 통해 데이터 조회
        // 도메인 객체를 애플리케이션 레이어의 DTO로 변환
        // 계좌 번호 조회
        String sourceAccountNumber = null;
        if (transaction.getSourceAccountId() != null) {
            sourceAccountNumber = accountPort.findById(transaction.getSourceAccountId())
                    .map(Account::getAccountNumber)
                    .orElse(null);
        }
        
        String targetAccountNumber = null;
        if (transaction.getTargetAccountId() != null) {
            targetAccountNumber = accountPort.findById(transaction.getTargetAccountId())
                    .map(Account::getAccountNumber)
                    .orElse(null);
        }
        
        return TransactionRead.builder()
                .id(transaction.getId())
                .sourceAccountId(transaction.getSourceAccountId())
                .sourceAccountNumber(sourceAccountNumber)
                .targetAccountId(transaction.getTargetAccountId())
                .targetAccountNumber(targetAccountNumber)
                .amount(transaction.getAmount())
                .type(transaction.getType().name())
                .status(transaction.getStatus().name())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
