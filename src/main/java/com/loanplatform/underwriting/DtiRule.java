package com.loanplatform.underwriting;

import com.loanplatform.application.Decision;
import com.loanplatform.application.LoanApplication;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Debt-to-income: monthly debt payments / gross monthly income.
// <= 36% is a common guideline; 43% was the long-standing Qualified Mortgage cap.
@Component
public class DtiRule implements UnderwritingRule {

    static final BigDecimal APPROVE_MAX = new BigDecimal("0.36");
    static final BigDecimal REFER_MAX = new BigDecimal("0.43");

    @Override
    public RuleResult evaluate(LoanApplication app) {
        BigDecimal monthlyIncome = app.getAnnualIncome().divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        BigDecimal dti = app.getMonthlyDebt().divide(monthlyIncome, 4, RoundingMode.HALF_UP);
        String pct = Percent.format(dti);

        if (dti.compareTo(APPROVE_MAX) <= 0) {
            return new RuleResult(Decision.APPROVE, "DTI " + pct + " is within 36%");
        }
        if (dti.compareTo(REFER_MAX) <= 0) {
            return new RuleResult(Decision.REFER, "DTI " + pct + " is between 36% and 43%");
        }
        return new RuleResult(Decision.DECLINE, "DTI " + pct + " is above 43%");
    }
}
