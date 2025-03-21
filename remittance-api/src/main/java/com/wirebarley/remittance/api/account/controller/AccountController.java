package com.wirebarley.remittance.api.account.controller;

import com.wirebarley.remittance.api.account.dto.CreateAccountRequest;
import com.wirebarley.remittance.api.account.dto.DepositRequest;
import com.wirebarley.remittance.api.account.dto.WithdrawRequest;
import com.wirebarley.remittance.application.account.command.CreateAccountCommand;
import com.wirebarley.remittance.application.account.command.DepositCommand;
import com.wirebarley.remittance.application.account.command.WithdrawCommand;
import com.wirebarley.remittance.application.account.read.AccountRead;
import com.wirebarley.remittance.application.account.usecase.AccountCommandUseCase;
import com.wirebarley.remittance.application.account.usecase.AccountQueryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "계좌 API", description = "계좌 등록, 조회, 입금, 출금 API")
public class AccountController {
    private final AccountQueryUseCase accountQueryUseCase ;
    private final AccountCommandUseCase accountCommandUseCase;

    @PostMapping
    @Operation(summary = "계좌 등록", description = "새로운 계좌를 등록합니다.")
    public ResponseEntity<UUID> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        CreateAccountCommand command = CreateAccountCommand.builder()
                .accountNumber(request.getAccountNumber())
                .accountName(request.getAccountName())
                .initialBalance(request.getInitialBalance())
                .build();
        
        UUID accountId = accountCommandUseCase.createAccount(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountId);
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "계좌 조회", description = "계좌 ID로 계좌를 조회합니다.")
    public ResponseEntity<AccountRead> getAccount(@PathVariable UUID accountId) {
        AccountRead accountRead = accountQueryUseCase.getAccount(accountId);
        return ResponseEntity.ok(accountRead);
    }

    @GetMapping("/number/{accountNumber}")
    @Operation(summary = "계좌번호로 계좌 조회", description = "계좌번호로 계좌를 조회합니다.")
    public ResponseEntity<AccountRead> getAccountByNumber(@PathVariable String accountNumber) {
        AccountRead accountRead = accountQueryUseCase.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(accountRead);
    }

    @GetMapping
    @Operation(summary = "모든 계좌 조회", description = "모든 계좌를 조회합니다.")
    public ResponseEntity<List<AccountRead>> getAllAccounts() {
        List<AccountRead> accountReads = accountQueryUseCase.getAllAccounts();
        return ResponseEntity.ok(accountReads);
    }

    @PostMapping("/{accountId}/deposit")
    @Operation(summary = "입금", description = "계좌에 입금합니다.")
    public ResponseEntity<UUID> deposit(@PathVariable UUID accountId, @Valid @RequestBody DepositRequest request) {
        DepositCommand command = DepositCommand.builder()
                .accountId(accountId)
                .amount(request.getAmount())
                .description(request.getDescription())
                .build();
        
        UUID updatedAccountId = accountCommandUseCase.deposit(command);
        return ResponseEntity.ok(updatedAccountId);
    }

    @PostMapping("/{accountId}/withdraw")
    @Operation(summary = "출금", description = "계좌에서 출금합니다.")
    public ResponseEntity<UUID> withdraw(@PathVariable UUID accountId, @Valid @RequestBody WithdrawRequest request) {
        WithdrawCommand command = WithdrawCommand.builder()
                .accountId(accountId)
                .amount(request.getAmount())
                .description(request.getDescription())
                .build();
        
        UUID updatedAccountId = accountCommandUseCase.withdraw(command);
        return ResponseEntity.ok(updatedAccountId);
    }
}
