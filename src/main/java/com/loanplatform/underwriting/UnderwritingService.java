package com.loanplatform.underwriting;

import com.loanplatform.application.Decision;
import com.loanplatform.application.LoanApplication;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class UnderwritingService {

    private final List<UnderwritingRule> rules;

    // Spring injects every UnderwritingRule bean (DtiRule, LtvRule, CreditScoreRule)
    public UnderwritingService(List<UnderwritingRule> rules) {
        this.rules = rules;
    }

    public UnderwritingResult evaluate(LoanApplication app) {
        List<RuleResult> results = rules.stream()
                .map(rule -> rule.evaluate(app))
                .toList();

        // Worst verdict wins. Enum order is APPROVE < REFER < DECLINE, so max() = most severe.
        Decision decision = results.stream()
                .map(RuleResult::decision)
                .max(Comparator.naturalOrder())
                .orElse(Decision.REFER);   // no rules configured -> a human decides

        List<String> reasons = results.stream()
                .map(RuleResult::reason)
                .toList();

        return new UnderwritingResult(decision, reasons);
    }
}
