package com.wirebarley.remittance.infrastructure.account.adapter;

import com.wirebarley.remittance.domain.account.Account;
import com.wirebarley.remittance.domain.account.port.AccountPort;
import com.wirebarley.remittance.infrastructure.account.entity.AccountEntity;
import com.wirebarley.remittance.infrastructure.account.repository.AccountRepository;
import com.wirebarley.remittance.common.util.AES256Util;
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
public class AccountAdapter implements AccountPort {
    private final AccountRepository accountRepository;
    private final AES256Util aes256Util;

    @Override
    public Account save(Account account) {
        AccountEntity entity = AccountEntity.fromDomain(account);
        AccountEntity savedEntity = accountRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return accountRepository.findById(id)
                .map(AccountEntity::toDomain);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        // 조회 시 암호화된 계좌번호로 조회되므로 암호화를 먼저 수행
        String encryptedAccountNumber = aes256Util.encrypt(accountNumber);
        return accountRepository.findByAccountNumber(encryptedAccountNumber)
                .map(AccountEntity::toDomain);
    }
    
    /**
     * 마스킹된 계좌번호로 계좌 조회
     * @param maskedAccountNumber 마스킹된 계좌번호 패턴
     * @return 도메인 계좌 객체 목록
     */
    @Override
    public List<Account> findByMaskedAccountNumber(String maskedAccountNumber) {
        return accountRepository.findByMaskedAccountNumberContaining(maskedAccountNumber).stream()
                .map(AccountEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll().stream()
                .map(AccountEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        accountRepository.deleteById(id);
    }
}
