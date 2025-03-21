package com.wirebarley.remittance.application.transaction.service;

import com.wirebarley.remittance.application.transaction.command.TransferCommand;
import com.wirebarley.remittance.common.util.TimeBasedUuidGenerator;
import com.wirebarley.remittance.domain.account.Account;
import com.wirebarley.remittance.domain.account.port.AccountPort;
import com.wirebarley.remittance.domain.transaction.Transaction;
import com.wirebarley.remittance.domain.transaction.TransactionStatus;
import com.wirebarley.remittance.domain.transaction.TransactionType;
import com.wirebarley.remittance.domain.transaction.port.TransactionPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionReadCommandServiceTest {

    @Mock
    private TransactionPort transactionPort;

    @Mock
    private AccountPort accountPort;

    @InjectMocks
    private TransactionCommandService transactionCommandService;

    @Test
    @DisplayName("이체 테스트")
    void transfer() {
        // given
        UUID sourceAccountId = TimeBasedUuidGenerator.generate();
        UUID targetAccountId = TimeBasedUuidGenerator.generate();
        TransferCommand command = TransferCommand.builder()
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(BigDecimal.valueOf(500))
                .description("테스트 이체")
                .build();

        Account sourceAccount = Account.builder()
                .id(sourceAccountId)
                .accountNumber("1234567890")
                .accountName("출금 계좌")
                .balance(BigDecimal.valueOf(1000))
                .build();

        Account targetAccount = Account.builder()
                .id(targetAccountId)
                .accountNumber("0987654321")
                .accountName("입금 계좌")
                .balance(BigDecimal.valueOf(2000))
                .build();

        Transaction pendingTransaction = Transaction.builder()
                .id(TimeBasedUuidGenerator.generate())
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(BigDecimal.valueOf(500))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .description("테스트 이체")
                .build();

        Transaction completedTransaction = Transaction.builder()
                .id(pendingTransaction.getId())
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(BigDecimal.valueOf(500))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description("테스트 이체")
                .build();

        when(accountPort.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountPort.findById(targetAccountId)).thenReturn(Optional.of(targetAccount));
        when(transactionPort.save(any(Transaction.class))).thenReturn(pendingTransaction, completedTransaction);

        // when
        UUID transactionId = transactionCommandService.transfer(command);

        // then
        assertNotNull(transactionId);
        assertEquals(pendingTransaction.getId(), transactionId);
        verify(accountPort).findById(sourceAccountId);
        verify(accountPort).findById(targetAccountId);
        verify(accountPort, times(2)).save(any(Account.class));
        verify(transactionPort, times(2)).save(any(Transaction.class));
    }
}
