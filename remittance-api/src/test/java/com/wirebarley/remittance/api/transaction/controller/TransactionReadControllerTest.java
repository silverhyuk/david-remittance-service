package com.wirebarley.remittance.api.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirebarley.remittance.api.transaction.dto.TransferRequest;
import com.wirebarley.remittance.application.transaction.read.TransactionRead;
import com.wirebarley.remittance.application.transaction.service.TransactionCommandService;
import com.wirebarley.remittance.application.transaction.service.TransactionQueryService;
import com.wirebarley.remittance.common.util.TimeBasedUuidGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionReadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionQueryService transactionQueryService;

    @MockBean
    private TransactionCommandService transactionCommandService;

    @Test
    @DisplayName("이체 API 테스트")
    void transfer() throws Exception {
        // given
        UUID sourceAccountId = TimeBasedUuidGenerator.generate();
        UUID targetAccountId = TimeBasedUuidGenerator.generate();
        TransferRequest request = TransferRequest.builder()
                .targetAccountId(targetAccountId)
                .amount(BigDecimal.valueOf(500))
                .description("테스트 이체")
                .build();

        UUID transactionId = TimeBasedUuidGenerator.generate();
        when(transactionCommandService.transfer(any())).thenReturn(transactionId);

        // when & then
        mockMvc.perform(post("/api/transactions/transfer/{sourceAccountId}", sourceAccountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(transactionId.toString()));
    }

    @Test
    @DisplayName("트랜잭션 조회 API 테스트")
    void getTransaction() throws Exception {
        // given
        UUID transactionId = TimeBasedUuidGenerator.generate();
        UUID sourceAccountId = TimeBasedUuidGenerator.generate();
        UUID targetAccountId = TimeBasedUuidGenerator.generate();
        
        TransactionRead transactionRead = TransactionRead.builder()
                .id(transactionId)
                .sourceAccountId(sourceAccountId)
                .sourceAccountNumber("1234567890")
                .targetAccountId(targetAccountId)
                .targetAccountNumber("0987654321")
                .amount(BigDecimal.valueOf(500))
                .type("TRANSFER")
                .status("COMPLETED")
                .description("테스트 이체")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(transactionQueryService.getTransaction(transactionId)).thenReturn(transactionRead);

        // when & then
        mockMvc.perform(get("/api/transactions/{transactionId}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.sourceAccountId").value(sourceAccountId.toString()))
                .andExpect(jsonPath("$.sourceAccountNumber").value("1234567890"))
                .andExpect(jsonPath("$.targetAccountId").value(targetAccountId.toString()))
                .andExpect(jsonPath("$.targetAccountNumber").value("0987654321"))
                .andExpect(jsonPath("$.amount").value(500))
                .andExpect(jsonPath("$.type").value("TRANSFER"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.description").value("테스트 이체"));
    }

    @Test
    @DisplayName("출금 계좌 기준 트랜잭션 조회 API 테스트")
    void getTransactionsBySourceAccount() throws Exception {
        // given
        UUID accountId = TimeBasedUuidGenerator.generate();
        
        TransactionRead transactionRead1 = TransactionRead.builder()
                .id(TimeBasedUuidGenerator.generate())
                .sourceAccountId(accountId)
                .sourceAccountNumber("1234567890")
                .targetAccountId(TimeBasedUuidGenerator.generate())
                .targetAccountNumber("0987654321")
                .amount(BigDecimal.valueOf(500))
                .type("TRANSFER")
                .status("COMPLETED")
                .description("테스트 이체 1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TransactionRead transactionRead2 = TransactionRead.builder()
                .id(TimeBasedUuidGenerator.generate())
                .sourceAccountId(accountId)
                .sourceAccountNumber("1234567890")
                .targetAccountId(TimeBasedUuidGenerator.generate())
                .targetAccountNumber("5555555555")
                .amount(BigDecimal.valueOf(300))
                .type("TRANSFER")
                .status("COMPLETED")
                .description("테스트 이체 2")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<TransactionRead> transactionReads = Arrays.asList(transactionRead1, transactionRead2);
        when(transactionQueryService.getTransactionsBySourceAccount(accountId)).thenReturn(transactionReads);

        // when & then
        mockMvc.perform(get("/api/transactions/source-account/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sourceAccountId").value(accountId.toString()))
                .andExpect(jsonPath("$[1].sourceAccountId").value(accountId.toString()))
                .andExpect(jsonPath("$[0].amount").value(500))
                .andExpect(jsonPath("$[1].amount").value(300));
    }

    @Test
    @DisplayName("입금 계좌 기준 트랜잭션 조회 API 테스트")
    void getTransactionsByTargetAccount() throws Exception {
        // given
        UUID accountId = TimeBasedUuidGenerator.generate();
        
        TransactionRead transactionRead1 = TransactionRead.builder()
                .id(TimeBasedUuidGenerator.generate())
                .sourceAccountId(TimeBasedUuidGenerator.generate())
                .sourceAccountNumber("1111111111")
                .targetAccountId(accountId)
                .targetAccountNumber("0987654321")
                .amount(BigDecimal.valueOf(500))
                .type("TRANSFER")
                .status("COMPLETED")
                .description("테스트 이체 1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TransactionRead transactionRead2 = TransactionRead.builder()
                .id(TimeBasedUuidGenerator.generate())
                .sourceAccountId(TimeBasedUuidGenerator.generate())
                .sourceAccountNumber("2222222222")
                .targetAccountId(accountId)
                .targetAccountNumber("0987654321")
                .amount(BigDecimal.valueOf(300))
                .type("TRANSFER")
                .status("COMPLETED")
                .description("테스트 이체 2")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<TransactionRead> transactionReads = Arrays.asList(transactionRead1, transactionRead2);
        when(transactionQueryService.getTransactionsByTargetAccount(accountId)).thenReturn(transactionReads);

        // when & then
        mockMvc.perform(get("/api/transactions/target-account/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].targetAccountId").value(accountId.toString()))
                .andExpect(jsonPath("$[1].targetAccountId").value(accountId.toString()))
                .andExpect(jsonPath("$[0].amount").value(500))
                .andExpect(jsonPath("$[1].amount").value(300));
    }
}
