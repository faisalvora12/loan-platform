package com.loanplatform.underwriting;

import com.loanplatform.application.Decision;

// One rule's verdict and why
public record RuleResult(Decision decision, String reason) {
}
