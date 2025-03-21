package com.wirebarley.remittance.application.account.usecase;

import com.wirebarley.remittance.application.account.read.AccountRead;

import java.util.List;
import java.util.UUID;

/**
 * 계좌 Query UseCase
 */
public interface AccountQueryUseCase {
    /**
     * 계좌 조회
     * @param accountId 계좌 ID
     * @return 계좌 DTO
     */
    AccountRead getAccount(UUID accountId);
    
    /**
     * 계좌번호로 계좌 조회
     * @param accountNumber 계좌번호
     * @return 계좌 DTO
     */
    AccountRead getAccountByNumber(String accountNumber);
    
    /**
     * 모든 계좌 조회
     * @return 계좌 DTO 목록
     */
    List<AccountRead> getAllAccounts();
}
