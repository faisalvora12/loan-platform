package com.loanplatform.underwriting;

import com.loanplatform.application.LoanApplication;

import java.math.BigDecimal;

// Builds applications for tests. Defaults pass every rule; each test changes only what it checks.
final class TestApplications {

    private TestApplications() {
    }

    static LoanApplication withDti(String monthlyDebt) {
        // income 120,000/yr = 10,000/mo, so monthlyDebt 3600 -> DTI 36%
        return new LoanApplication("Test", new BigDecimal("120000"), new BigDecimal(monthlyDebt),
                new BigDecimal("300000"), new BigDecimal("400000"), 740);
    }

    static LoanApplication withLtv(String loanAmount) {
        // property 400,000, so loanAmount 320000 -> LTV 80%
        return new LoanApplication("Test", new BigDecimal("120000"), new BigDecimal("3000"),
                new BigDecimal(loanAmount), new BigDecimal("400000"), 740);
    }

    static LoanApplication withCreditScore(int score) {
        return new LoanApplication("Test", new BigDecimal("120000"), new BigDecimal("3000"),
                new BigDecimal("300000"), new BigDecimal("400000"), score);
    }
}
