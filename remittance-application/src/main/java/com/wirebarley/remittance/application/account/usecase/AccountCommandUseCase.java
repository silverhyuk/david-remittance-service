package com.wirebarley.remittance.application.account.usecase;

import com.wirebarley.remittance.application.account.command.CreateAccountCommand;
import com.wirebarley.remittance.application.account.command.DepositCommand;
import com.wirebarley.remittance.application.account.command.WithdrawCommand;

import java.util.UUID;

/**
 * 계좌 Command UseCase
 */
public interface AccountCommandUseCase {
    /**
     * 계좌 생성
     * @param command 계좌 생성 커맨드
     * @return 생성된 계좌 ID
     */
    UUID createAccount(CreateAccountCommand command);
    
    /**
     * 입금 처리
     * @param command 입금 커맨드
     * @return 입금 후 계좌 ID
     */
    UUID deposit(DepositCommand command);
    
    /**
     * 출금 처리
     * @param command 출금 커맨드
     * @return 출금 후 계좌 ID
     */
    UUID withdraw(WithdrawCommand command);
}
