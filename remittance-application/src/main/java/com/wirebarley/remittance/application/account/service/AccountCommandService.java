package com.wirebarley.remittance.application.account.service;

import com.wirebarley.remittance.application.account.command.CreateAccountCommand;
import com.wirebarley.remittance.application.account.command.DepositCommand;
import com.wirebarley.remittance.application.account.command.WithdrawCommand;
import com.wirebarley.remittance.application.account.usecase.AccountCommandUseCase;
import com.wirebarley.remittance.common.aop.DistributedLock;
import com.wirebarley.remittance.common.error.BusinessException;
import com.wirebarley.remittance.common.error.ErrorCode;
import com.wirebarley.remittance.domain.account.Account;
import com.wirebarley.remittance.domain.account.port.AccountPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 계좌 Command 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountCommandService implements AccountCommandUseCase {
    private final AccountPort accountPort;

    /**
     * 계좌 생성 
     * @param command 계좌 생성 커맨드
     * @return 생성된 계좌 ID
     */
    @Transactional
    @DistributedLock(
            key = "deposit",
            paramNames = {"command.accountNumber"},
            waitTime = 5L,
            leaseTime = 10L
    )
    public UUID createAccount(CreateAccountCommand command) {
        log.debug("계좌 생성 시작: {}", command.getAccountNumber());
        
        // 계좌번호 중복 검사
        accountPort.findByAccountNumber(command.getAccountNumber())
                .ifPresent(account -> {
                    log.error("계좌번호 중복: {}", command.getAccountNumber());
                    throw new BusinessException(ErrorCode.DUPLICATE_ACCOUNT_NUMBER, 
                            "이미 존재하는 계좌번호입니다: " + command.getAccountNumber());
                });
        
        try {
            // 계좌 생성
            Account account = Account.builder()
                    .accountNumber(command.getAccountNumber())
                    .accountName(command.getAccountName())
                    .balance(command.getInitialBalance())
                    .build();
            
            account = accountPort.save(account);
            log.info("계좌 생성 완료: {}, ID: {}", account.getAccountNumber(), account.getId());
            
            return account.getId();
        } catch (Exception e) {
            log.error("계좌 생성 실패: {}", command.getAccountNumber(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "계좌 생성 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 입금 처리 
     * @param command 입금 커맨드
     * @return 입금 후 계좌 ID
     */
    @Transactional
    @DistributedLock(
        key = "deposit",
        paramNames = {"command.accountId"},
        waitTime = 5L,
        leaseTime = 10L
    )
    public UUID deposit(DepositCommand command) {
        log.debug("입금 처리 시작: 계좌 ID: {}, 금액: {}", command.getAccountId(), command.getAmount());
        
        try {
            Account account = accountPort.findById(command.getAccountId())
                    .orElseThrow(() -> {
                        log.error("계좌를 찾을 수 없음: {}", command.getAccountId());
                        return new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, 
                                "계좌를 찾을 수 없습니다: " + command.getAccountId());
                    });
            
            try {
                account.deposit(command.getAmount());
                account = accountPort.save(account);
                log.info("입금 처리 완료: 계좌 ID: {}, 금액: {}, 잔액: {}", 
                        account.getId(), command.getAmount(), account.getBalance());
                
                return account.getId();
            } catch (IllegalStateException e) {
                log.error("입금 처리 실패: 계좌 상태 오류: {}", command.getAccountId(), e);
                throw new BusinessException(ErrorCode.INACTIVE_ACCOUNT, e.getMessage(), e);
            } catch (Exception e) {
                log.error("입금 처리 실패: {}", command.getAccountId(), e);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "입금 처리 중 오류가 발생했습니다.", e);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("입금 처리 중 예외 발생: {}", command.getAccountId(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "입금 처리 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 출금 처리 
     * @param command 출금 커맨드
     * @return 출금 후 계좌 ID
     */
    @Transactional
    @DistributedLock(
        key = "withdraw",
        paramNames = {"command.accountId"},
        waitTime = 5L,
        leaseTime = 10L
    )
    public UUID withdraw(WithdrawCommand command) {
        log.debug("출금 처리 시작: 계좌 ID: {}, 금액: {}", command.getAccountId(), command.getAmount());
        
        try {
            Account account = accountPort.findById(command.getAccountId())
                    .orElseThrow(() -> {
                        log.error("계좌를 찾을 수 없음: {}", command.getAccountId());
                        return new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, 
                                "계좌를 찾을 수 없습니다: " + command.getAccountId());
                    });
            
            try {
                account.withdraw(command.getAmount());
                account = accountPort.save(account);
                log.info("출금 처리 완료: 계좌 ID: {}, 금액: {}, 잔액: {}", 
                        account.getId(), command.getAmount(), account.getBalance());
                
                return account.getId();
            } catch (IllegalStateException e) {
                log.error("출금 처리 실패: 계좌 상태 오류: {}", command.getAccountId(), e);
                throw new BusinessException(ErrorCode.INACTIVE_ACCOUNT, e.getMessage(), e);
            } catch (IllegalArgumentException e) {
                log.error("출금 처리 실패: 유효하지 않은 금액: {}", command.getAccountId(), e);
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, e.getMessage(), e);
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("잔액이 부족")) {
                    log.error("출금 처리 실패: 잔액 부족: {}", command.getAccountId(), e);
                    throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE, e.getMessage(), e);
                }
                log.error("출금 처리 실패: {}", command.getAccountId(), e);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "출금 처리 중 오류가 발생했습니다.", e);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("출금 처리 중 예외 발생: {}", command.getAccountId(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "출금 처리 중 오류가 발생했습니다.", e);
        }
    }
}
