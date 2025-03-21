package com.wirebarley.remittance.application.account.service;

import com.wirebarley.remittance.application.account.read.AccountRead;
import com.wirebarley.remittance.application.account.usecase.AccountQueryUseCase;
import com.wirebarley.remittance.domain.account.port.AccountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 계좌 Query 서비스
 */
@Service
@RequiredArgsConstructor
public class AccountQueryService implements AccountQueryUseCase {
    private final AccountPort accountPort;

    /**
     * 계좌 조회 
     * @param accountId 계좌 ID
     * @return 계좌 DTO
     */
    @Transactional(readOnly = true)
    public AccountRead getAccount(UUID accountId) {
        com.wirebarley.remittance.domain.account.Account account = accountPort.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다: " + accountId));
        
        return mapToDto(account);
    }
    
    /**
     * 계좌번호로 계좌 조회 
     * @param accountNumber 계좌번호
     * @return 계좌 DTO
     */
    @Transactional(readOnly = true)
    public AccountRead getAccountByNumber(String accountNumber) {
        com.wirebarley.remittance.domain.account.Account account = accountPort.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다: " + accountNumber));
        
        return mapToDto(account);
    }
    
    /**
     * 모든 계좌 조회 
     * @return 계좌 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<AccountRead> getAllAccounts() {
        return accountPort.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 도메인 객체를 DTO로 변환
     * @param account 도메인 계좌 객체
     * @return 계좌 DTO
     */
    private AccountRead mapToDto(com.wirebarley.remittance.domain.account.Account account) {
        return AccountRead.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountName(account.getAccountName())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
