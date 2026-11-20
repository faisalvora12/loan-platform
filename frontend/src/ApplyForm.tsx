import { useState, type FormEvent } from 'react';
import { submitApplication, ValidationError } from './api';
import type { FieldErrors, LoanApplicationResponse } from './types';

interface Props {
  onDecision: (result: LoanApplicationResponse) => void;
}

const FIELDS = [
  { name: 'applicantName', label: 'Applicant name', type: 'text' },
  { name: 'annualIncome', label: 'Annual income ($)', type: 'number' },
  { name: 'monthlyDebt', label: 'Monthly debt payments, incl. this loan ($)', type: 'number' },
  { name: 'loanAmount', label: 'Loan amount ($)', type: 'number' },
  { name: 'propertyValue', label: 'Property value ($)', type: 'number' },
  { name: 'creditScore', label: 'Credit score (300–850)', type: 'number' },
] as const;

type FieldName = (typeof FIELDS)[number]['name'];

const EMPTY: Record<FieldName, string> = {
  applicantName: '', annualIncome: '', monthlyDebt: '', loanAmount: '', propertyValue: '', creditScore: '',
};

export function ApplyForm({ onDecision }: Props) {
  // Controlled inputs: React state is the single source of truth for the form values
  const [values, setValues] = useState(EMPTY);
  const [errors, setErrors] = useState<FieldErrors>({});
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState<string | null>(null);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setSubmitting(true);
    setErrors({});
    setServerError(null);

    // Empty number fields go as null so the API's @NotNull reports them
    const toNumber = (v: string) => (v.trim() === '' ? null : Number(v));

    try {
      const result = await submitApplication({
        applicantName: values.applicantName,
        annualIncome: toNumber(values.annualIncome)!,
        monthlyDebt: toNumber(values.monthlyDebt)!,
        loanAmount: toNumber(values.loanAmount)!,
        propertyValue: toNumber(values.propertyValue)!,
        creditScore: toNumber(values.creditScore)!,
      });
      onDecision(result);
    } catch (err) {
      if (err instanceof ValidationError) {
        setErrors(err.fieldErrors);   // the backend is the source of truth for validation
      } else {
        setServerError('Something went wrong. Please try again.');
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} noValidate>
      <h1>Loan application</h1>
      {FIELDS.map((f) => (
        <label key={f.name}>
          {f.label}
          <input
            type={f.type}
            value={values[f.name]}
            onChange={(e) => setValues({ ...values, [f.name]: e.target.value })}
            aria-invalid={!!errors[f.name]}
          />
          {errors[f.name] && <span className="error">{errors[f.name]}</span>}
        </label>
      ))}
      {serverError && <p className="error">{serverError}</p>}
      <button type="submit" disabled={submitting}>
        {submitting ? 'Submitting…' : 'Submit application'}
      </button>
    </form>
  );
}
