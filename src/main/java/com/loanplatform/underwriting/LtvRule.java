package com.loanplatform.underwriting;

import com.loanplatform.application.Decision;
import com.loanplatform.application.LoanApplication;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Loan-to-value: loan amount / property value.
// <= 80% means a 20% down payment (no mortgage insurance); above 95% is too little equity.
@Component
public class LtvRule implements UnderwritingRule {

    static final BigDecimal APPROVE_MAX = new BigDecimal("0.80");
    static final BigDecimal REFER_MAX = new BigDecimal("0.95");

    @Override
    public RuleResult evaluate(LoanApplication app) {
        BigDecimal ltv = app.getLoanAmount().divide(app.getPropertyValue(), 4, RoundingMode.HALF_UP);
        String pct = Percent.format(ltv);

        if (ltv.compareTo(APPROVE_MAX) <= 0) {
            return new RuleResult(Decision.APPROVE, "LTV " + pct + " is within 80%");
        }
        if (ltv.compareTo(REFER_MAX) <= 0) {
            return new RuleResult(Decision.REFER, "LTV " + pct + " is between 80% and 95%");
        }
        return new RuleResult(Decision.DECLINE, "LTV " + pct + " is above 95%");
    }
}
