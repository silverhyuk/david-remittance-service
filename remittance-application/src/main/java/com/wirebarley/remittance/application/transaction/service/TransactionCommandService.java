package com.wirebarley.remittance.application.transaction.service;

import com.wirebarley.remittance.application.transaction.command.TransferCommand;
import com.wirebarley.remittance.application.transaction.usecase.TransactionCommandUseCase;
import com.wirebarley.remittance.common.aop.DistributedLock;
import com.wirebarley.remittance.common.error.BusinessException;
import com.wirebarley.remittance.common.error.ErrorCode;
import com.wirebarley.remittance.domain.account.Account;
import com.wirebarley.remittance.domain.account.port.AccountPort;
import com.wirebarley.remittance.domain.transaction.Transaction;
import com.wirebarley.remittance.domain.transaction.TransactionStatus;
import com.wirebarley.remittance.domain.transaction.TransactionType;
import com.wirebarley.remittance.domain.transaction.port.TransactionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 트랜잭션 Command 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCommandService implements TransactionCommandUseCase {
    private final TransactionPort transactionPort;
    private final AccountPort accountPort;
    
    /**
     * 이체 처리
     * @param command 이체 커맨드
     * @return 생성된 트랜잭션 ID
     */
    @Transactional
    @DistributedLock(
        key = "transfer",
        paramNames = {"command.sourceAccountId", "command.targetAccountId"},
        waitTime = 10L,
        leaseTime = 15L
    )
    public UUID transfer(TransferCommand command) {
        log.debug("이체 처리 시작: 출금 계좌: {}, 입금 계좌: {}, 금액: {}", 
                command.getSourceAccountId(), command.getTargetAccountId(), command.getAmount());
        
        // 동일 계좌 이체 검증
        if (command.getSourceAccountId().equals(command.getTargetAccountId())) {
            log.error("동일 계좌 이체 시도: {}", command.getSourceAccountId());
            throw new BusinessException(ErrorCode.SAME_ACCOUNT_TRANSFER, "동일한 계좌 간 이체는 불가능합니다.");
        }
        
        // 계좌 조회
        Account sourceAccount = accountPort.findById(command.getSourceAccountId())
                .orElseThrow(() -> {
                    log.error("출금 계좌를 찾을 수 없음: {}", command.getSourceAccountId());
                    return new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, 
                            "출금 계좌를 찾을 수 없습니다: " + command.getSourceAccountId());
                });
        
        Account targetAccount = accountPort.findById(command.getTargetAccountId())
                .orElseThrow(() -> {
                    log.error("입금 계좌를 찾을 수 없음: {}", command.getTargetAccountId());
                    return new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, 
                            "입금 계좌를 찾을 수 없습니다: " + command.getTargetAccountId());
                });
        
        // 트랜잭션 생성
        Transaction transaction = Transaction.builder()
                .sourceAccountId(command.getSourceAccountId())
                .targetAccountId(command.getTargetAccountId())
                .amount(command.getAmount())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .description(command.getDescription())
                .build();
        
        transaction = transactionPort.save(transaction);
        log.debug("트랜잭션 생성 완료: {}", transaction.getId());
        
        try {
            // 출금 처리
            try {
                sourceAccount.withdraw(command.getAmount());
                accountPort.save(sourceAccount);
                log.debug("출금 처리 완료: 계좌: {}, 금액: {}", sourceAccount.getId(), command.getAmount());
            } catch (IllegalStateException e) {
                log.error("출금 계좌가 비활성화 상태: {}", sourceAccount.getId(), e);
                throw new BusinessException(ErrorCode.INACTIVE_ACCOUNT, "출금 계좌가 비활성화 상태입니다.", e);
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("잔액이 부족")) {
                    log.error("잔액 부족: 계좌: {}, 잔액: {}, 출금액: {}", 
                            sourceAccount.getId(), sourceAccount.getBalance(), command.getAmount(), e);
                    throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE, 
                            "잔액이 부족합니다. 현재 잔액: " + sourceAccount.getBalance(), e);
                }
                throw new BusinessException(ErrorCode.TRANSFER_FAILED, "출금 처리 중 오류가 발생했습니다.", e);
            }
            
            // 입금 처리
            try {
                targetAccount.deposit(command.getAmount());
                accountPort.save(targetAccount);
                log.debug("입금 처리 완료: 계좌: {}, 금액: {}", targetAccount.getId(), command.getAmount());
            } catch (IllegalStateException e) {
                log.error("입금 계좌가 비활성화 상태: {}", targetAccount.getId(), e);
                throw new BusinessException(ErrorCode.INACTIVE_ACCOUNT, "입금 계좌가 비활성화 상태입니다.", e);
            } catch (Exception e) {
                log.error("입금 처리 실패: {}", targetAccount.getId(), e);
                throw new BusinessException(ErrorCode.TRANSFER_FAILED, "입금 처리 중 오류가 발생했습니다.", e);
            }
            
            // 트랜잭션 완료 처리
            transaction.complete();
            Transaction completedTransaction = transactionPort.save(transaction);
            log.info("이체 처리 완료: 트랜잭션 ID: {}, 출금 계좌: {}, 입금 계좌: {}, 금액: {}", 
                    completedTransaction.getId(), sourceAccount.getId(), targetAccount.getId(), command.getAmount());
            
            return completedTransaction.getId();
        } catch (BusinessException e) {
            // 실패 처리
            transaction.fail(e.getMessage());
            Transaction failedTransaction = transactionPort.save(transaction);
            log.error("이체 실패: 트랜잭션 ID: {}, 에러 코드: {}, 메시지: {}", 
                    failedTransaction.getId(), e.getErrorCode().getCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            // 실패 처리
            transaction.fail(e.getMessage());
            Transaction failedTransaction = transactionPort.save(transaction);
            log.error("이체 중 예외 발생: 트랜잭션 ID: {}", failedTransaction.getId(), e);
            throw new BusinessException(ErrorCode.TRANSFER_FAILED, "이체 처리 중 시스템 오류가 발생했습니다.", e);
        }
    }
}
