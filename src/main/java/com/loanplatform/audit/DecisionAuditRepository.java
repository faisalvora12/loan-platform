package com.loanplatform.audit;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DecisionAuditRepository extends MongoRepository<DecisionAudit, String> {

    List<DecisionAudit> findByApplicationIdOrderByDecidedAtDesc(Long applicationId);
}
