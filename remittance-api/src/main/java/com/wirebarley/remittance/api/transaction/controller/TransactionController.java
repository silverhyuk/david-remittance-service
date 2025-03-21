package com.wirebarley.remittance.api.transaction.controller;

import com.wirebarley.remittance.api.transaction.dto.TransferRequest;
import com.wirebarley.remittance.application.transaction.command.TransferCommand;
import com.wirebarley.remittance.application.transaction.read.TransactionRead;
import com.wirebarley.remittance.application.transaction.usecase.TransactionCommandUseCase;
import com.wirebarley.remittance.application.transaction.usecase.TransactionQueryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "트랜잭션 API", description = "이체 및 트랜잭션 조회 API")
public class TransactionController {
    private final TransactionCommandUseCase transactionCommandUseCase;
    private final TransactionQueryUseCase transactionQueryUseCase;

    @PostMapping("/transfer/{sourceAccountId}")
    @Operation(summary = "이체", description = "한 계좌에서 다른 계좌로 이체합니다.")
    public ResponseEntity<UUID> transfer(@PathVariable UUID sourceAccountId, @Valid @RequestBody TransferRequest request) {
        TransferCommand command = TransferCommand.builder()
                .sourceAccountId(sourceAccountId)
                .targetAccountId(request.getTargetAccountId())
                .amount(request.getAmount())
                .description(request.getDescription())
                .build();
        
        UUID transactionId = transactionCommandUseCase.transfer(command);
        return ResponseEntity.ok(transactionId);
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "트랜잭션 조회", description = "트랜잭션 ID로 트랜잭션을 조회합니다.")
    public ResponseEntity<TransactionRead> getTransaction(@PathVariable UUID transactionId) {
        TransactionRead transactionRead = transactionQueryUseCase.getTransaction(transactionId);
        return ResponseEntity.ok(transactionRead);
    }

    @GetMapping("/source-account/{accountId}")
    @Operation(summary = "출금 계좌 기준 트랜잭션 조회", description = "출금 계좌 ID로 트랜잭션을 조회합니다.")
    public ResponseEntity<List<TransactionRead>> getTransactionsBySourceAccount(@PathVariable UUID accountId) {
        List<TransactionRead> transactionReads = transactionQueryUseCase.getTransactionsBySourceAccount(accountId);
        return ResponseEntity.ok(transactionReads);
    }

    @GetMapping("/target-account/{accountId}")
    @Operation(summary = "입금 계좌 기준 트랜잭션 조회", description = "입금 계좌 ID로 트랜잭션을 조회합니다.")
    public ResponseEntity<List<TransactionRead>> getTransactionsByTargetAccount(@PathVariable UUID accountId) {
        List<TransactionRead> transactionReads = transactionQueryUseCase.getTransactionsByTargetAccount(accountId);
        return ResponseEntity.ok(transactionReads);
    }
}
