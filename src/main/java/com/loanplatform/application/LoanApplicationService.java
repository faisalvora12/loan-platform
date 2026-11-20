package com.loanplatform.application;

import com.loanplatform.application.dto.LoanApplicationRequest;
import com.loanplatform.application.dto.LoanApplicationResponse;
import com.loanplatform.audit.DecisionAudit;
import com.loanplatform.audit.DecisionAuditRepository;
import com.loanplatform.underwriting.UnderwritingResult;
import com.loanplatform.underwriting.UnderwritingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoanApplicationService {

    private final LoanApplicationRepository repository;
    private final UnderwritingService underwritingService;
    private final DecisionAuditRepository auditRepository;

    public LoanApplicationService(LoanApplicationRepository repository,
                                  UnderwritingService underwritingService,
                                  DecisionAuditRepository auditRepository) {
        this.repository = repository;
        this.underwritingService = underwritingService;
        this.auditRepository = auditRepository;
    }

    // One transaction: the application row and its reason rows commit together or not at all
    @Transactional
    public LoanApplicationResponse submit(LoanApplicationRequest request) {
        LoanApplication app = new LoanApplication(
                request.applicantName(),
                request.annualIncome(),
                request.monthlyDebt(),
                request.loanAmount(),
                request.propertyValue(),
                request.creditScore());

        UnderwritingResult result = underwritingService.evaluate(app);
        app.applyDecision(result.decision(), result.reasons());

        LoanApplication saved = repository.save(app);

        // Audit after the save so the record has the application id.
        // If this throws, the exception rolls back the Postgres transaction: no decision without an audit record.
        auditRepository.save(DecisionAudit.of(saved));

        return LoanApplicationResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public LoanApplicationResponse get(Long id) {
        return repository.findById(id)
                .map(LoanApplicationResponse::from)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> listByDecision(Decision decision) {
        return repository.findByDecisionOrderByCreatedAtDesc(decision).stream()
                .map(LoanApplicationResponse::from)
                .toList();
    }
}
