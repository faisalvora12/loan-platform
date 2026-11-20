package com.loanplatform.underwriting;

import com.loanplatform.application.Decision;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Tests how rule results combine. Uses fake rules (lambdas) so it doesn't depend on real thresholds.
class UnderwritingServiceTest {

    private static UnderwritingRule always(Decision decision, String reason) {
        return app -> new RuleResult(decision, reason);
    }

    @Test
    void approvesWhenEveryRuleApproves() {
        UnderwritingService service = new UnderwritingService(List.of(
                always(Decision.APPROVE, "a"), always(Decision.APPROVE, "b")));

        UnderwritingResult result = service.evaluate(TestApplications.withCreditScore(740));

        assertThat(result.decision()).isEqualTo(Decision.APPROVE);
        assertThat(result.reasons()).containsExactly("a", "b");
    }

    @Test
    void oneReferMakesTheWholeApplicationRefer() {
        UnderwritingService service = new UnderwritingService(List.of(
                always(Decision.APPROVE, "a"), always(Decision.REFER, "b")));

        assertThat(service.evaluate(TestApplications.withCreditScore(740)).decision())
                .isEqualTo(Decision.REFER);
    }

    @Test
    void declineWinsOverReferAndApprove() {
        UnderwritingService service = new UnderwritingService(List.of(
                always(Decision.REFER, "a"), always(Decision.DECLINE, "b"), always(Decision.APPROVE, "c")));

        UnderwritingResult result = service.evaluate(TestApplications.withCreditScore(740));

        assertThat(result.decision()).isEqualTo(Decision.DECLINE);
        assertThat(result.reasons()).hasSize(3);   // every reason is kept, not just the failing one
    }

    @Test
    void noRulesConfiguredFallsBackToRefer() {
        UnderwritingService service = new UnderwritingService(List.of());

        assertThat(service.evaluate(TestApplications.withCreditScore(740)).decision())
                .isEqualTo(Decision.REFER);
    }

    @Test
    void realRulesTogether() {
        UnderwritingService service = new UnderwritingService(List.of(
                new DtiRule(), new LtvRule(), new CreditScoreRule()));

        // DTI 30%, LTV 75%, score 650 -> credit score alone pushes it to REFER
        UnderwritingResult result = service.evaluate(TestApplications.withCreditScore(650));

        assertThat(result.decision()).isEqualTo(Decision.REFER);
        assertThat(result.reasons()).hasSize(3);
    }
}
