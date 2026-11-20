package com.loanplatform.application;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // Postgres generates the id
    private Long id;

    @Column(nullable = false)
    private String applicantName;

    // BigDecimal for money: exact decimal math, no floating-point rounding errors
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal annualIncome;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyDebt;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal propertyValue;

    @Column(nullable = false)
    private int creditScore;

    @Enumerated(EnumType.STRING)   // stores "APPROVE", not 0/1/2, so reordering the enum can't corrupt data
    @Column(nullable = false)
    private Decision decision;

    // Reasons live in their own table: loan_application_reasons(application_id, reason)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "loan_application_reasons", joinColumns = @JoinColumn(name = "application_id"))
    @Column(name = "reason")
    private List<String> reasons = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected LoanApplication() {
        // required by JPA
    }

    public LoanApplication(String applicantName, BigDecimal annualIncome, BigDecimal monthlyDebt,
                           BigDecimal loanAmount, BigDecimal propertyValue, int creditScore) {
        this.applicantName = applicantName;
        this.annualIncome = annualIncome;
        this.monthlyDebt = monthlyDebt;
        this.loanAmount = loanAmount;
        this.propertyValue = propertyValue;
        this.creditScore = creditScore;
        this.createdAt = Instant.now();
    }

    public void applyDecision(Decision decision, List<String> reasons) {
        this.decision = decision;
        this.reasons = new ArrayList<>(reasons);
    }

    public Long getId() { return id; }
    public String getApplicantName() { return applicantName; }
    public BigDecimal getAnnualIncome() { return annualIncome; }
    public BigDecimal getMonthlyDebt() { return monthlyDebt; }
    public BigDecimal getLoanAmount() { return loanAmount; }
    public BigDecimal getPropertyValue() { return propertyValue; }
    public int getCreditScore() { return creditScore; }
    public Decision getDecision() { return decision; }
    public List<String> getReasons() { return reasons; }
    public Instant getCreatedAt() { return createdAt; }
}
