package com.loanplatform.underwriting;

import com.loanplatform.application.Decision;

import java.util.List;

// The final decision plus every rule's reason
public record UnderwritingResult(Decision decision, List<String> reasons) {
}
