package com.wirebarley.remittance.api.account.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 출금 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequest {
    @NotNull(message = "출금액은 필수입니다.")
    @Positive(message = "출금액은 양수여야 합니다.")
    private BigDecimal amount;
    
    private String description;
}
