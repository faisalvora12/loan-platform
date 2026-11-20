package com.loanplatform.underwriting;

import com.loanplatform.application.LoanApplication;

// Each rule is its own class. Adding a rule = adding a new @Component; the service doesn't change.
public interface UnderwritingRule {

    RuleResult evaluate(LoanApplication app);
}
