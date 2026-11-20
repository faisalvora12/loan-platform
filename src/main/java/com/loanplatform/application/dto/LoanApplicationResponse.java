package com.loanplatform.application.dto;

import com.loanplatform.application.Decision;
import com.loanplatform.application.LoanApplication;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

// What the API returns. Keeps the entity (database shape) out of the API.
public record LoanApplicationResponse(
        Long id,
        String applicantName,
        BigDecimal loanAmount,
        int creditScore,
        Decision decision,
        List<String> reasons,
        Instant createdAt
) {
    public static LoanApplicationResponse from(LoanApplication app) {
        return new LoanApplicationResponse(
                app.getId(),
                app.getApplicantName(),
                app.getLoanAmount(),
                app.getCreditScore(),
                app.getDecision(),
                List.copyOf(app.getReasons()),
                app.getCreatedAt());
    }
}
