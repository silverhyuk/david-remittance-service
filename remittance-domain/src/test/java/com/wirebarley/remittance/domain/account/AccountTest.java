package com.wirebarley.remittance.domain.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    @DisplayName("계좌 생성 테스트")
    void createAccount() {
        // given
        String accountNumber = "1234567890";
        String accountName = "테스트 계좌";
        BigDecimal initialBalance = BigDecimal.valueOf(1000);

        // when
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountName(accountName)
                .balance(initialBalance)
                .build();

        // then
        assertNotNull(account.getId());
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(accountName, account.getAccountName());
        assertEquals(initialBalance, account.getBalance());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertNotNull(account.getCreatedAt());
        assertNotNull(account.getUpdatedAt());
    }

    @Test
    @DisplayName("입금 테스트")
    void deposit() {
        // given
        Account account = Account.builder()
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .balance(BigDecimal.valueOf(1000))
                .build();
        BigDecimal depositAmount = BigDecimal.valueOf(500);

        // when
        account.deposit(depositAmount);

        // then
        assertEquals(BigDecimal.valueOf(1500), account.getBalance());
    }

    @Test
    @DisplayName("출금 테스트")
    void withdraw() {
        // given
        Account account = Account.builder()
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .balance(BigDecimal.valueOf(1000))
                .build();
        BigDecimal withdrawAmount = BigDecimal.valueOf(500);

        // when
        account.withdraw(withdrawAmount);

        // then
        assertEquals(BigDecimal.valueOf(500), account.getBalance());
    }

    @Test
    @DisplayName("잔액 부족 시 출금 실패 테스트")
    void withdrawWithInsufficientBalance() {
        // given
        Account account = Account.builder()
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .balance(BigDecimal.valueOf(1000))
                .build();
        BigDecimal withdrawAmount = BigDecimal.valueOf(1500);

        // when & then
        assertThrows(IllegalStateException.class, () -> account.withdraw(withdrawAmount));
    }

    @Test
    @DisplayName("비활성 계좌 입금 실패 테스트")
    void depositToInactiveAccount() {
        // given
        Account account = Account.builder()
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.INACTIVE)
                .build();
        BigDecimal depositAmount = BigDecimal.valueOf(500);

        // when & then
        assertThrows(IllegalStateException.class, () -> account.deposit(depositAmount));
    }

    @Test
    @DisplayName("비활성 계좌 출금 실패 테스트")
    void withdrawFromInactiveAccount() {
        // given
        Account account = Account.builder()
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.INACTIVE)
                .build();
        BigDecimal withdrawAmount = BigDecimal.valueOf(500);

        // when & then
        assertThrows(IllegalStateException.class, () -> account.withdraw(withdrawAmount));
    }
}
