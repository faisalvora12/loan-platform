import type { FieldErrors, LoanApplicationRequest, LoanApplicationResponse } from './types';

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

// Thrown when the API returns 400, so the form can show each message next to its input
export class ValidationError extends Error {
  fieldErrors: FieldErrors;

  constructor(fieldErrors: FieldErrors) {
    super('Validation failed');
    this.fieldErrors = fieldErrors;
  }
}

export async function submitApplication(request: LoanApplicationRequest): Promise<LoanApplicationResponse> {
  const res = await fetch(`${API_URL}/api/applications`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });

  if (res.status === 400) {
    const problem = await res.json();
    throw new ValidationError(problem.errors ?? {});
  }
  if (!res.ok) {
    throw new Error(`Request failed (${res.status})`);
  }
  return res.json();
}
