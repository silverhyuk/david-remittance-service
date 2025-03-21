package com.wirebarley.remittance.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirebarley.remittance.api.RemittanceServiceApplication;
import com.wirebarley.remittance.api.account.dto.CreateAccountRequest;
import com.wirebarley.remittance.api.account.dto.DepositRequest;
import com.wirebarley.remittance.api.transaction.dto.TransferRequest;
import com.wirebarley.remittance.application.account.read.AccountRead;
import com.wirebarley.remittance.application.transaction.read.TransactionRead;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RemittanceServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RemittanceServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("계좌 생성 및 조회 통합 테스트")
    void createAndGetAccount() throws Exception {
        // 계좌 생성
        CreateAccountRequest request = CreateAccountRequest.builder()
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .initialBalance(BigDecimal.valueOf(1000))
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String accountIdStr = createResult.getResponse().getContentAsString();
        UUID accountId = UUID.fromString(accountIdStr);

        // 계좌 조회
        MvcResult getResult = mockMvc.perform(get("/api/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andReturn();

        AccountRead accountRead = objectMapper.readValue(getResult.getResponse().getContentAsString(), AccountRead.class);
        assertEquals("1234567890", accountRead.getAccountNumber());
        assertEquals("테스트 계좌", accountRead.getAccountName());
        assertEquals(0, BigDecimal.valueOf(1000).compareTo(accountRead.getBalance()));
        assertEquals("ACTIVE", accountRead.getStatus());
    }

    @Test
    @DisplayName("입금 통합 테스트")
    void deposit() throws Exception {
        // 계좌 생성
        CreateAccountRequest createRequest = CreateAccountRequest.builder()
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .initialBalance(BigDecimal.valueOf(1000))
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String accountIdStr = createResult.getResponse().getContentAsString();
        UUID accountId = UUID.fromString(accountIdStr);

        // 입금
        DepositRequest depositRequest = DepositRequest.builder()
                .amount(BigDecimal.valueOf(500))
                .description("테스트 입금")
                .build();

        mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk());

        // 계좌 조회
        MvcResult getResult = mockMvc.perform(get("/api/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andReturn();

        AccountRead accountRead = objectMapper.readValue(getResult.getResponse().getContentAsString(), AccountRead.class);
        assertEquals(0, BigDecimal.valueOf(1500).compareTo(accountRead.getBalance()));
    }

    @Test
    @DisplayName("이체 통합 테스트")
    void transfer() throws Exception {
        // 출금 계좌 생성
        CreateAccountRequest sourceRequest = CreateAccountRequest.builder()
                .accountNumber("1234567890")
                .accountName("출금 계좌")
                .initialBalance(BigDecimal.valueOf(1000))
                .build();

        MvcResult sourceResult = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sourceRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String sourceAccountIdStr = sourceResult.getResponse().getContentAsString();
        UUID sourceAccountId = UUID.fromString(sourceAccountIdStr);

        // 입금 계좌 생성
        CreateAccountRequest targetRequest = CreateAccountRequest.builder()
                .accountNumber("0987654321")
                .accountName("입금 계좌")
                .initialBalance(BigDecimal.valueOf(2000))
                .build();

        MvcResult targetResult = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(targetRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String targetAccountIdStr = targetResult.getResponse().getContentAsString();
        UUID targetAccountId = UUID.fromString(targetAccountIdStr);

        // 이체
        TransferRequest transferRequest = TransferRequest.builder()
                .targetAccountId(targetAccountId)
                .amount(BigDecimal.valueOf(500))
                .description("테스트 이체")
                .build();

        MvcResult transferResult = mockMvc.perform(post("/api/transactions/transfer/{sourceAccountId}", sourceAccountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String transactionIdStr = transferResult.getResponse().getContentAsString();
        UUID transactionId = UUID.fromString(transactionIdStr);

        // 트랜잭션 조회
        MvcResult getTransactionResult = mockMvc.perform(get("/api/transactions/{transactionId}", transactionId))
                .andExpect(status().isOk())
                .andReturn();

        TransactionRead transactionRead = objectMapper.readValue(getTransactionResult.getResponse().getContentAsString(), TransactionRead.class);
        assertEquals(sourceAccountId, transactionRead.getSourceAccountId());
        assertEquals(targetAccountId, transactionRead.getTargetAccountId());
        assertEquals(0, BigDecimal.valueOf(500).compareTo(transactionRead.getAmount()));
        assertEquals("TRANSFER", transactionRead.getType());
        assertEquals("COMPLETED", transactionRead.getStatus());

        // 출금 계좌 조회
        MvcResult getSourceResult = mockMvc.perform(get("/api/accounts/{accountId}", sourceAccountId))
                .andExpect(status().isOk())
                .andReturn();

        AccountRead sourceAccountRead = objectMapper.readValue(getSourceResult.getResponse().getContentAsString(), AccountRead.class);
        assertEquals(0, BigDecimal.valueOf(500).compareTo(sourceAccountRead.getBalance()));

        // 입금 계좌 조회
        MvcResult getTargetResult = mockMvc.perform(get("/api/accounts/{accountId}", targetAccountId))
                .andExpect(status().isOk())
                .andReturn();

        AccountRead targetAccountRead = objectMapper.readValue(getTargetResult.getResponse().getContentAsString(), AccountRead.class);
        assertEquals(0, BigDecimal.valueOf(2500).compareTo(targetAccountRead.getBalance()));
    }
}
