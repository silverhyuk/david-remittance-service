package com.wirebarley.remittance.application.account.service;

import com.wirebarley.remittance.application.account.command.CreateAccountCommand;
import com.wirebarley.remittance.application.account.command.DepositCommand;
import com.wirebarley.remittance.application.account.command.WithdrawCommand;
import com.wirebarley.remittance.common.util.TimeBasedUuidGenerator;
import com.wirebarley.remittance.domain.account.Account;
import com.wirebarley.remittance.domain.account.port.AccountPort;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountReadCommandServiceTest {

    @Mock
    private AccountPort accountPort;

    @InjectMocks
    private AccountCommandService accountCommandService;

    @Test
    @DisplayName("계좌 생성 테스트")
    void createAccount() {
        // given
        CreateAccountCommand command = CreateAccountCommand.builder()
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .initialBalance(BigDecimal.valueOf(1000))
                .build();

        Account savedAccount = Account.builder()
                .id(TimeBasedUuidGenerator.generate())
                .accountNumber(command.getAccountNumber())
                .accountName(command.getAccountName())
                .balance(command.getInitialBalance())
                .build();

        when(accountPort.findByAccountNumber(command.getAccountNumber())).thenReturn(Optional.empty());
        when(accountPort.save(any(Account.class))).thenReturn(savedAccount);

        // when
        UUID accountId = accountCommandService.createAccount(command);

        // then
        assertNotNull(accountId);
        verify(accountPort).findByAccountNumber(command.getAccountNumber());
        verify(accountPort).save(any(Account.class));
    }

    @Test
    @DisplayName("계좌번호 중복 시 계좌 생성 실패 테스트")
    void createAccountWithDuplicateNumber() {
        // given
        CreateAccountCommand command = CreateAccountCommand.builder()
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .initialBalance(BigDecimal.valueOf(1000))
                .build();

        Account existingAccount = Account.builder()
                .id(TimeBasedUuidGenerator.generate())
                .accountNumber(command.getAccountNumber())
                .accountName("기존 계좌")
                .balance(BigDecimal.valueOf(2000))
                .build();

        when(accountPort.findByAccountNumber(command.getAccountNumber())).thenReturn(Optional.of(existingAccount));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> accountCommandService.createAccount(command));
        verify(accountPort).findByAccountNumber(command.getAccountNumber());
        verify(accountPort, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("입금 테스트")
    void deposit() {
        // given
        UUID accountId = TimeBasedUuidGenerator.generate();
        DepositCommand command = DepositCommand.builder()
                .accountId(accountId)
                .amount(BigDecimal.valueOf(500))
                .description("테스트 입금")
                .build();

        Account account = Account.builder()
                .id(accountId)
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .balance(BigDecimal.valueOf(1000))
                .build();

        Account updatedAccount = Account.builder()
                .id(accountId)
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .balance(BigDecimal.valueOf(1500))
                .build();

        when(accountPort.findById(accountId)).thenReturn(Optional.of(account));
        when(accountPort.save(any(Account.class))).thenReturn(updatedAccount);

        // when
        UUID resultId = accountCommandService.deposit(command);

        // then
        assertEquals(accountId, resultId);
        verify(accountPort).findById(accountId);
        verify(accountPort).save(any(Account.class));
    }

    @Test
    @DisplayName("출금 테스트")
    void withdraw() {
        // given
        UUID accountId = TimeBasedUuidGenerator.generate();
        WithdrawCommand command = WithdrawCommand.builder()
                .accountId(accountId)
                .amount(BigDecimal.valueOf(500))
                .description("테스트 출금")
                .build();

        Account account = Account.builder()
                .id(accountId)
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .balance(BigDecimal.valueOf(1000))
                .build();

        Account updatedAccount = Account.builder()
                .id(accountId)
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .balance(BigDecimal.valueOf(500))
                .build();

        when(accountPort.findById(accountId)).thenReturn(Optional.of(account));
        when(accountPort.save(any(Account.class))).thenReturn(updatedAccount);

        // when
        UUID resultId = accountCommandService.withdraw(command);

        // then
        assertEquals(accountId, resultId);
        verify(accountPort).findById(accountId);
        verify(accountPort).save(any(Account.class));
    }
}
