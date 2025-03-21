package com.wirebarley.remittance.api.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 계좌 생성 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    @NotBlank(message = "계좌번호는 필수입니다.")
    private String accountNumber;
    
    @NotBlank(message = "계좌명은 필수입니다.")
    private String accountName;
    
    @NotNull(message = "초기 잔액은 필수입니다.")
    @Positive(message = "초기 잔액은 양수여야 합니다.")
    private BigDecimal initialBalance;
}
