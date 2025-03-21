package com.wirebarley.remittance.application.transaction.service;

import com.wirebarley.remittance.application.transaction.read.TransactionRead;
import com.wirebarley.remittance.common.util.TimeBasedUuidGenerator;
import com.wirebarley.remittance.domain.account.Account;
import com.wirebarley.remittance.domain.account.port.AccountPort;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionReadQueryServiceTest {

    @Mock
    private TransactionPort transactionPort;

    @Mock
    private AccountPort accountPort;

    @InjectMocks
    private TransactionQueryService transactionQueryService;

    @Test
    @DisplayName("트랜잭션 조회 테스트")
    void getTransaction() {
        // given
        UUID transactionId = TimeBasedUuidGenerator.generate();
        UUID sourceAccountId = TimeBasedUuidGenerator.generate();
        UUID targetAccountId = TimeBasedUuidGenerator.generate();
        
        com.wirebarley.remittance.domain.transaction.Transaction transaction = com.wirebarley.remittance.domain.transaction.Transaction.builder()
                .id(transactionId)
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(BigDecimal.valueOf(500))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
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

        when(transactionPort.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(accountPort.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountPort.findById(targetAccountId)).thenReturn(Optional.of(targetAccount));

        // when
        TransactionRead transactionReadDto = transactionQueryService.getTransaction(transactionId);

        // then
        assertNotNull(transactionReadDto);
        assertEquals(transactionId, transactionReadDto.getId());
        assertEquals(sourceAccountId, transactionReadDto.getSourceAccountId());
        assertEquals("1234567890", transactionReadDto.getSourceAccountNumber());
        assertEquals(targetAccountId, transactionReadDto.getTargetAccountId());
        assertEquals("0987654321", transactionReadDto.getTargetAccountNumber());
        assertEquals(BigDecimal.valueOf(500), transactionReadDto.getAmount());
        assertEquals("TRANSFER", transactionReadDto.getType());
        assertEquals("COMPLETED", transactionReadDto.getStatus());
        assertEquals("테스트 이체", transactionReadDto.getDescription());
        verify(transactionPort).findById(transactionId);
        verify(accountPort).findById(sourceAccountId);
        verify(accountPort).findById(targetAccountId);
    }

    @Test
    @DisplayName("출금 계좌 기준 트랜잭션 조회 테스트")
    void getTransactionsBySourceAccount() {
        // given
        UUID sourceAccountId = TimeBasedUuidGenerator.generate();
        
        com.wirebarley.remittance.domain.transaction.Transaction transaction1 = com.wirebarley.remittance.domain.transaction.Transaction.builder()
                .id(TimeBasedUuidGenerator.generate())
                .sourceAccountId(sourceAccountId)
                .targetAccountId(TimeBasedUuidGenerator.generate())
                .amount(BigDecimal.valueOf(500))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description("테스트 이체 1")
                .build();

        com.wirebarley.remittance.domain.transaction.Transaction transaction2 = com.wirebarley.remittance.domain.transaction.Transaction.builder()
                .id(TimeBasedUuidGenerator.generate())
                .sourceAccountId(sourceAccountId)
                .targetAccountId(TimeBasedUuidGenerator.generate())
                .amount(BigDecimal.valueOf(300))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description("테스트 이체 2")
                .build();

        when(transactionPort.findBySourceAccountId(sourceAccountId)).thenReturn(Arrays.asList(transaction1, transaction2));
        when(accountPort.findById(any(UUID.class))).thenReturn(Optional.empty());

        // when
        List<TransactionRead> transactionReads = transactionQueryService.getTransactionsBySourceAccount(sourceAccountId);

        // then
        assertNotNull(transactionReads);
        assertEquals(2, transactionReads.size());
        verify(transactionPort).findBySourceAccountId(sourceAccountId);
    }

    @Test
    @DisplayName("입금 계좌 기준 트랜잭션 조회 테스트")
    void getTransactionsByTargetAccount() {
        // given
        UUID targetAccountId = TimeBasedUuidGenerator.generate();
        
        com.wirebarley.remittance.domain.transaction.Transaction transaction1 = com.wirebarley.remittance.domain.transaction.Transaction.builder()
                .id(TimeBasedUuidGenerator.generate())
                .sourceAccountId(TimeBasedUuidGenerator.generate())
                .targetAccountId(targetAccountId)
                .amount(BigDecimal.valueOf(500))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description("테스트 이체 1")
                .build();

        com.wirebarley.remittance.domain.transaction.Transaction transaction2 = com.wirebarley.remittance.domain.transaction.Transaction.builder()
                .id(TimeBasedUuidGenerator.generate())
                .sourceAccountId(TimeBasedUuidGenerator.generate())
                .targetAccountId(targetAccountId)
                .amount(BigDecimal.valueOf(300))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description("테스트 이체 2")
                .build();

        when(transactionPort.findByTargetAccountId(targetAccountId)).thenReturn(Arrays.asList(transaction1, transaction2));
        when(accountPort.findById(any(UUID.class))).thenReturn(Optional.empty());

        // when
        List<TransactionRead> transactionReads = transactionQueryService.getTransactionsByTargetAccount(targetAccountId);

        // then
        assertNotNull(transactionReads);
        assertEquals(2, transactionReads.size());
        verify(transactionPort).findByTargetAccountId(targetAccountId);
    }
}
