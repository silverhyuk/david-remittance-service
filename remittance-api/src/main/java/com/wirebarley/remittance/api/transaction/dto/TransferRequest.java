package com.wirebarley.remittance.api.transaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 이체 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    @NotNull(message = "대상 계좌 ID는 필수입니다.")
    private UUID targetAccountId;
    
    @NotNull(message = "이체액은 필수입니다.")
    @Positive(message = "이체액은 양수여야 합니다.")
    private BigDecimal amount;
    
    private String description;
}
