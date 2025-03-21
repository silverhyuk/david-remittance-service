package com.wirebarley.remittance.infrastructure.account.adapter;

import com.wirebarley.remittance.domain.account.Account;
import com.wirebarley.remittance.domain.account.port.AccountPort;
import com.wirebarley.remittance.infrastructure.account.entity.AccountEntity;
import com.wirebarley.remittance.infrastructure.account.repository.JpaAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 계좌 리포지토리 어댑터
 * 헥사고날 아키텍처의 어댑터(Adapter) - 외부 시스템과 도메인을 연결
 */
@Repository
@RequiredArgsConstructor
public class AccountPortAdapter implements AccountPort {
    private final JpaAccountRepository jpaAccountRepository;

    @Override
    public Account save(Account account) {
        AccountEntity entity = AccountEntity.fromDomain(account);
        AccountEntity savedEntity = jpaAccountRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return jpaAccountRepository.findById(id)
                .map(AccountEntity::toDomain);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return jpaAccountRepository.findByAccountNumber(accountNumber)
                .map(AccountEntity::toDomain);
    }

    @Override
    public List<Account> findAll() {
        return jpaAccountRepository.findAll().stream()
                .map(AccountEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaAccountRepository.deleteById(id);
    }
}
