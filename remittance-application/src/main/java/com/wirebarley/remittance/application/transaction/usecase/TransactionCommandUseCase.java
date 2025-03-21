package com.wirebarley.remittance.application.transaction.usecase;

import com.wirebarley.remittance.application.transaction.command.TransferCommand;

import java.util.UUID;

/**
 * 트랜잭션 Command UseCase
 */
public interface TransactionCommandUseCase {
    /**
     * 이체 처리
     * @param command 이체 커맨드
     * @return 생성된 트랜잭션 ID
     */
    UUID transfer(TransferCommand command);
}
