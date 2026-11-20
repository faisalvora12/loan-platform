// Same shapes as the Java DTOs (LoanApplicationRequest / LoanApplicationResponse)

export type Decision = 'APPROVE' | 'REFER' | 'DECLINE';

export interface LoanApplicationRequest {
  applicantName: string;
  annualIncome: number;
  monthlyDebt: number;
  loanAmount: number;
  propertyValue: number;
  creditScore: number;
}

export interface LoanApplicationResponse {
  id: number;
  applicantName: string;
  loanAmount: number;
  creditScore: number;
  decision: Decision;
  reasons: string[];
  createdAt: string;
}

// field name -> message, from the API's 400 ProblemDetail "errors"
export type FieldErrors = Record<string, string>;
