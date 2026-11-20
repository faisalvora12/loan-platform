package com.loanplatform.application;

import com.loanplatform.application.dto.LoanApplicationRequest;
import com.loanplatform.application.dto.LoanApplicationResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class LoanApplicationController {

    private final LoanApplicationService service;

    // Constructor injection: dependency is final, explicit, and easy to mock in tests
    public LoanApplicationController(LoanApplicationService service) {
        this.service = service;
    }

    // POST /api/applications -> 201 Created + Location header + the decision
    @PostMapping
    public ResponseEntity<LoanApplicationResponse> submit(@Valid @RequestBody LoanApplicationRequest request) {
        LoanApplicationResponse response = service.submit(request);
        return ResponseEntity
                .created(URI.create("/api/applications/" + response.id()))
                .body(response);
    }

    // GET /api/applications/42 -> 200, or 404 via the exception handler
    @GetMapping("/{id}")
    public LoanApplicationResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    // GET /api/applications?decision=REFER -> the referral queue
    @GetMapping
    public List<LoanApplicationResponse> listByDecision(@RequestParam Decision decision) {
        return service.listByDecision(decision);
    }
}
