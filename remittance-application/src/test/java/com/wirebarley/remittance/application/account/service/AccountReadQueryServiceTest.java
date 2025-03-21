package com.wirebarley.remittance.application.account.service;

import com.wirebarley.remittance.application.account.read.AccountRead;
import com.wirebarley.remittance.common.util.TimeBasedUuidGenerator;
import com.wirebarley.remittance.domain.account.AccountStatus;
import com.wirebarley.remittance.domain.account.port.AccountPort;
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
class AccountReadQueryServiceTest {

    @Mock
    private AccountPort accountPort;

    @InjectMocks
    private AccountQueryService accountQueryService;

    @Test
    @DisplayName("계좌 조회 테스트")
    void getAccount() {
        // given
        UUID accountId = TimeBasedUuidGenerator.generate();
        com.wirebarley.remittance.domain.account.Account account = com.wirebarley.remittance.domain.account.Account.builder()
                .id(accountId)
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountPort.findById(accountId)).thenReturn(Optional.of(account));

        // when
        AccountRead accountReadDto = accountQueryService.getAccount(accountId);

        // then
        assertNotNull(accountReadDto);
        assertEquals(accountId, accountReadDto.getId());
        assertEquals("1234567890", accountReadDto.getAccountNumber());
        assertEquals("테스트 계좌", accountReadDto.getAccountName());
        assertEquals(BigDecimal.valueOf(1000), accountReadDto.getBalance());
        assertEquals("ACTIVE", accountReadDto.getStatus());
        verify(accountPort).findById(accountId);
    }

    @Test
    @DisplayName("계좌번호로 계좌 조회 테스트")
    void getAccountByNumber() {
        // given
        String accountNumber = "1234567890";
        com.wirebarley.remittance.domain.account.Account account = com.wirebarley.remittance.domain.account.Account.builder()
                .id(TimeBasedUuidGenerator.generate())
                .accountNumber(accountNumber)
                .accountName("테스트 계좌")
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountPort.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        // when
        AccountRead accountReadDto = accountQueryService.getAccountByNumber(accountNumber);

        // then
        assertNotNull(accountReadDto);
        assertEquals(accountNumber, accountReadDto.getAccountNumber());
        assertEquals("테스트 계좌", accountReadDto.getAccountName());
        assertEquals(BigDecimal.valueOf(1000), accountReadDto.getBalance());
        assertEquals("ACTIVE", accountReadDto.getStatus());
        verify(accountPort).findByAccountNumber(accountNumber);
    }

    @Test
    @DisplayName("모든 계좌 조회 테스트")
    void getAllAccounts() {
        // given
        com.wirebarley.remittance.domain.account.Account account1 = com.wirebarley.remittance.domain.account.Account.builder()
                .id(TimeBasedUuidGenerator.generate())
                .accountNumber("1234567890")
                .accountName("테스트 계좌 1")
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .build();

        com.wirebarley.remittance.domain.account.Account account2 = com.wirebarley.remittance.domain.account.Account.builder()
                .id(TimeBasedUuidGenerator.generate())
                .accountNumber("0987654321")
                .accountName("테스트 계좌 2")
                .balance(BigDecimal.valueOf(2000))
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountPort.findAll()).thenReturn(Arrays.asList(account1, account2));

        // when
        List<AccountRead> accountReads = accountQueryService.getAllAccounts();

        // then
        assertNotNull(accountReads);
        assertEquals(2, accountReads.size());
        verify(accountPort).findAll();
    }
}
