package com.loanplatform.underwriting;

import com.loanplatform.application.Decision;
import com.loanplatform.application.LoanApplication;
import org.springframework.stereotype.Component;

// 620 is the usual minimum for a conventional loan; 680+ is a comfortable approve.
@Component
public class CreditScoreRule implements UnderwritingRule {

    static final int APPROVE_MIN = 680;
    static final int REFER_MIN = 620;

    @Override
    public RuleResult evaluate(LoanApplication app) {
        int score = app.getCreditScore();

        if (score >= APPROVE_MIN) {
            return new RuleResult(Decision.APPROVE, "Credit score " + score + " is 680 or higher");
        }
        if (score >= REFER_MIN) {
            return new RuleResult(Decision.REFER, "Credit score " + score + " is between 620 and 679");
        }
        return new RuleResult(Decision.DECLINE, "Credit score " + score + " is below 620");
    }
}
