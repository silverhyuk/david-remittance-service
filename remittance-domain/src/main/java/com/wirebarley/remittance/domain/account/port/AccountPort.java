package com.wirebarley.remittance.domain.account.port;

import com.wirebarley.remittance.domain.account.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 계좌 리포지토리 포트 인터페이스
 * 헥사고날 아키텍처의 포트(Port) - 도메인에서 외부로 향하는 인터페이스
 */
public interface AccountPort {
    /**
     * 계좌 저장
     * @param account 저장할 계좌
     * @return 저장된 계좌
     */
    Account save(Account account);
    
    /**
     * ID로 계좌 조회
     * @param id 계좌 ID
     * @return 계좌 Optional
     */
    Optional<Account> findById(UUID id);
    
    /**
     * 계좌번호로 계좌 조회
     * @param accountNumber 계좌번호
     * @return 계좌 Optional
     */
    Optional<Account> findByAccountNumber(String accountNumber);
    
    /**
     * 모든 계좌 조회
     * @return 계좌 목록
     */
    List<Account> findAll();
    
    /**
     * 계좌 삭제
     * @param id 삭제할 계좌 ID
     */
    void deleteById(UUID id);
}
