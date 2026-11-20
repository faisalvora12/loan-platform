package com.loanplatform.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

// What the client sends. A record: immutable, constructor/getters/equals generated.
public record LoanApplicationRequest(
        @NotBlank String applicantName,
        @NotNull @Positive BigDecimal annualIncome,
        // total monthly debt payments, including the payment on this new loan
        @NotNull @PositiveOrZero BigDecimal monthlyDebt,
        @NotNull @Positive BigDecimal loanAmount,
        @NotNull @Positive BigDecimal propertyValue,
        @NotNull @Min(300) @Max(850) Integer creditScore
) {
}
