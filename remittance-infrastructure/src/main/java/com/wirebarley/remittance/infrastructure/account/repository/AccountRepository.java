package com.wirebarley.remittance.infrastructure.account.repository;

import com.wirebarley.remittance.infrastructure.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA 계좌 리포지토리
 */
@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    /**
     * 계좌번호로 계좌 조회
     * @param accountNumber 계좌번호
     * @return 계좌 Optional
     */
    Optional<AccountEntity> findByAccountNumber(String accountNumber);
    
    /**
     * 마스킹된 계좌번호로 계좌 조회
     * @param maskedAccountNumber 마스킹된 계좌번호 패턴
     * @return 계좌 목록
     */
    List<AccountEntity> findByMaskedAccountNumberContaining(String maskedAccountNumber);
}
