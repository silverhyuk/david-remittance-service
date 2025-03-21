package com.wirebarley.remittance.api.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirebarley.remittance.api.account.dto.CreateAccountRequest;
import com.wirebarley.remittance.api.account.dto.DepositRequest;
import com.wirebarley.remittance.api.account.dto.WithdrawRequest;
import com.wirebarley.remittance.application.account.read.AccountRead;
import com.wirebarley.remittance.application.account.service.AccountCommandService;
import com.wirebarley.remittance.application.account.service.AccountQueryService;
import com.wirebarley.remittance.common.util.TimeBasedUuidGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
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

@WebMvcTest(controllers = AccountController.class,
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)
@AutoConfigureMockMvc
@MockBean(JpaMetamodelMappingContext.class)
class AccountReadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountQueryService accountQueryService;

    @MockBean
    private AccountCommandService accountCommandService;

    @Test
    @DisplayName("계좌 생성 API 테스트")
    void createAccount() throws Exception {
        // given
        CreateAccountRequest request = CreateAccountRequest.builder()
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .initialBalance(BigDecimal.valueOf(1000))
                .build();

        UUID accountId = TimeBasedUuidGenerator.generate();
        when(accountCommandService.createAccount(any())).thenReturn(accountId);

        // when & then
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string(accountId.toString()));
    }

    @Test
    @DisplayName("계좌 조회 API 테스트")
    void getAccount() throws Exception {
        // given
        UUID accountId = TimeBasedUuidGenerator.generate();
        AccountRead accountRead = AccountRead.builder()
                .id(accountId)
                .accountNumber("1234567890")
                .accountName("테스트 계좌")
                .balance(BigDecimal.valueOf(1000))
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(accountQueryService.getAccount(accountId)).thenReturn(accountRead);

        // when & then
        mockMvc.perform(get("/api/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.accountName").value("테스트 계좌"))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("계좌번호로 계좌 조회 API 테스트")
    void getAccountByNumber() throws Exception {
        // given
        String accountNumber = "1234567890";
        AccountRead accountRead = AccountRead.builder()
                .id(TimeBasedUuidGenerator.generate())
                .accountNumber(accountNumber)
                .accountName("테스트 계좌")
                .balance(BigDecimal.valueOf(1000))
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(accountQueryService.getAccountByNumber(accountNumber)).thenReturn(accountRead);

        // when & then
        mockMvc.perform(get("/api/accounts/number/{accountNumber}", accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountNumber))
                .andExpect(jsonPath("$.accountName").value("테스트 계좌"))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("모든 계좌 조회 API 테스트")
    void getAllAccounts() throws Exception {
        // given
        AccountRead accountRead1 = AccountRead.builder()
                .id(TimeBasedUuidGenerator.generate())
                .accountNumber("1234567890")
                .accountName("테스트 계좌 1")
                .balance(BigDecimal.valueOf(1000))
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        AccountRead accountRead2 = AccountRead.builder()
                .id(TimeBasedUuidGenerator.generate())
                .accountNumber("0987654321")
                .accountName("테스트 계좌 2")
                .balance(BigDecimal.valueOf(2000))
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<AccountRead> accountReads = Arrays.asList(accountRead1, accountRead2);
        when(accountQueryService.getAllAccounts()).thenReturn(accountReads);

        // when & then
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
                .andExpect(jsonPath("$[1].accountNumber").value("0987654321"))
                .andExpect(jsonPath("$[0].accountName").value("테스트 계좌 1"))
                .andExpect(jsonPath("$[1].accountName").value("테스트 계좌 2"));
    }

    @Test
    @DisplayName("입금 API 테스트")
    void deposit() throws Exception {
        // given
        UUID accountId = TimeBasedUuidGenerator.generate();
        DepositRequest request = DepositRequest.builder()
                .amount(BigDecimal.valueOf(500))
                .description("테스트 입금")
                .build();

        when(accountCommandService.deposit(any())).thenReturn(accountId);

        // when & then
        mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(accountId.toString()));
    }

    @Test
    @DisplayName("출금 API 테스트")
    void withdraw() throws Exception {
        // given
        UUID accountId = TimeBasedUuidGenerator.generate();
        WithdrawRequest request = WithdrawRequest.builder()
                .amount(BigDecimal.valueOf(500))
                .description("테스트 출금")
                .build();

        when(accountCommandService.withdraw(any())).thenReturn(accountId);

        // when & then
        mockMvc.perform(post("/api/accounts/{accountId}/withdraw", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(accountId.toString()));
    }
}
