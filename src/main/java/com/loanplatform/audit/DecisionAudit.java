package com.loanplatform.audit;

import com.loanplatform.application.Decision;
import com.loanplatform.application.LoanApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

// One document per underwriting decision: a snapshot of what was decided, on which inputs, and why.
// Append-only: never updated, so it shows exactly what the system saw at that moment.
@Document(collection = "decision_audit")
public record DecisionAudit(
        @Id String id,
        @Indexed Long applicationId,
        Inputs inputs,
        Decision decision,
        List<String> reasons,
        Instant decidedAt
) {
    // Nested object: stored inside the same document, no join needed
    public record Inputs(BigDecimal annualIncome, BigDecimal monthlyDebt,
                         BigDecimal loanAmount, BigDecimal propertyValue, int creditScore) {
    }

    public static DecisionAudit of(LoanApplication app) {
        Inputs inputs = new Inputs(app.getAnnualIncome(), app.getMonthlyDebt(),
                app.getLoanAmount(), app.getPropertyValue(), app.getCreditScore());
        return new DecisionAudit(null, app.getId(), inputs,
                app.getDecision(), List.copyOf(app.getReasons()), Instant.now());
    }
}
