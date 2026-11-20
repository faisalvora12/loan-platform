package com.loanplatform.application;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Spring Data generates the implementation at startup: save, findById, findAll, delete ...
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    // Derived query: Spring builds "where decision = ? order by created_at desc" from the method name
    List<LoanApplication> findByDecisionOrderByCreatedAtDesc(Decision decision);
}
