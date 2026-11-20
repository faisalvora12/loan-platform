package com.loanplatform.application;

public class ApplicationNotFoundException extends RuntimeException {

    public ApplicationNotFoundException(Long id) {
        super("Loan application " + id + " not found");
    }
}
