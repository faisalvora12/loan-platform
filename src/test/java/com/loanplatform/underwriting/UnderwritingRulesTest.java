package com.loanplatform.underwriting;

import com.loanplatform.application.Decision;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

// Plain unit tests: no Spring context, no database. Each rule is tested at and just past its thresholds.
class UnderwritingRulesTest {

    private final DtiRule dtiRule = new DtiRule();
    private final LtvRule ltvRule = new LtvRule();
    private final CreditScoreRule creditScoreRule = new CreditScoreRule();

    @ParameterizedTest(name = "monthly debt {0} -> {1}")
    @CsvSource({
            "3000, APPROVE",   // 30%
            "3600, APPROVE",   // exactly 36%: boundary is inclusive
            "3601, REFER",     // 36.01%
            "4300, REFER",     // exactly 43%
            "4301, DECLINE"    // 43.01%
    })
    void dtiRule(String monthlyDebt, Decision expected) {
        RuleResult result = dtiRule.evaluate(TestApplications.withDti(monthlyDebt));

        assertThat(result.decision()).isEqualTo(expected);
        assertThat(result.reason()).startsWith("DTI");
    }

    @ParameterizedTest(name = "loan {0} on 400k -> {1}")
    @CsvSource({
            "300000, APPROVE",   // 75%
            "320000, APPROVE",   // exactly 80%
            "320400, REFER",     // 80.1%
            "380000, REFER",     // exactly 95%
            "380400, DECLINE"    // 95.1%
    })
    void ltvRule(String loanAmount, Decision expected) {
        RuleResult result = ltvRule.evaluate(TestApplications.withLtv(loanAmount));

        assertThat(result.decision()).isEqualTo(expected);
        assertThat(result.reason()).startsWith("LTV");
    }

    @ParameterizedTest(name = "score {0} -> {1}")
    @CsvSource({
            "850, APPROVE",
            "680, APPROVE",
            "679, REFER",
            "620, REFER",
            "619, DECLINE",
            "300, DECLINE"
    })
    void creditScoreRule(int score, Decision expected) {
        RuleResult result = creditScoreRule.evaluate(TestApplications.withCreditScore(score));

        assertThat(result.decision()).isEqualTo(expected);
        assertThat(result.reason()).contains(String.valueOf(score));
    }
}
