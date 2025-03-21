package com.wirebarley.remittance.infrastructure.transaction.adapter;

import com.wirebarley.remittance.domain.transaction.Transaction;
import com.wirebarley.remittance.domain.transaction.TransactionStatus;
import com.wirebarley.remittance.domain.transaction.TransactionType;
import com.wirebarley.remittance.domain.transaction.port.TransactionPort;
import com.wirebarley.remittance.infrastructure.transaction.entity.TransactionEntity;
import com.wirebarley.remittance.infrastructure.transaction.repository.JpaTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 트랜잭션 리포지토리 어댑터
 * 헥사고날 아키텍처의 어댑터(Adapter) - 외부 시스템과 도메인을 연결
 */
@Repository
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionPort {
    private final JpaTransactionRepository jpaTransactionRepository;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = TransactionEntity.fromDomain(transaction);
        TransactionEntity savedEntity = jpaTransactionRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return jpaTransactionRepository.findById(id)
                .map(TransactionEntity::toDomain);
    }

    @Override
    public List<Transaction> findBySourceAccountId(UUID accountId) {
        return jpaTransactionRepository.findBySourceAccountId(accountId).stream()
                .map(TransactionEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByTargetAccountId(UUID accountId) {
        return jpaTransactionRepository.findByTargetAccountId(accountId).stream()
                .map(TransactionEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByType(TransactionType type) {
        return jpaTransactionRepository.findByType(type).stream()
                .map(TransactionEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByStatus(TransactionStatus status) {
        return jpaTransactionRepository.findByStatus(status).stream()
                .map(TransactionEntity::toDomain)
                .collect(Collectors.toList());
    }
}
