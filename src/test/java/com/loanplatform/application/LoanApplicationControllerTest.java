package com.loanplatform.application;

import com.loanplatform.application.dto.LoanApplicationRequest;
import com.loanplatform.application.dto.LoanApplicationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Web layer only: controller + validation + exception handler. The service is a Mockito mock,
// so no database is needed and the test runs in about a second.
@WebMvcTest(LoanApplicationController.class)
class LoanApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanApplicationService service;

    private static final String VALID_BODY = """
            {"applicantName":"Ana Lopez","annualIncome":120000,"monthlyDebt":3000,
             "loanAmount":300000,"propertyValue":400000,"creditScore":740}
            """;

    @Test
    void validRequestReturns201WithLocationAndDecision() throws Exception {
        LoanApplicationResponse response = new LoanApplicationResponse(1L, "Ana Lopez",
                new BigDecimal("300000"), 740, Decision.APPROVE, List.of("ok"), Instant.now());
        when(service.submit(any(LoanApplicationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/applications").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/applications/1"))
                .andExpect(jsonPath("$.decision").value("APPROVE"));
    }

    @Test
    void invalidRequestReturns400WithFieldErrorsAndNeverCallsService() throws Exception {
        String badBody = """
                {"applicantName":"","annualIncome":-5,"monthlyDebt":3000,
                 "loanAmount":300000,"propertyValue":400000,"creditScore":900}
                """;

        mockMvc.perform(post("/api/applications").contentType(MediaType.APPLICATION_JSON).content(badBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.applicantName").exists())
                .andExpect(jsonPath("$.errors.annualIncome").exists())
                .andExpect(jsonPath("$.errors.creditScore").exists());

        verify(service, never()).submit(any());
    }

    @Test
    void unknownIdReturns404() throws Exception {
        when(service.get(999L)).thenThrow(new ApplicationNotFoundException(999L));

        mockMvc.perform(get("/api/applications/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Loan application 999 not found"));
    }
}
