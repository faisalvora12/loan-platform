import { Component, EventEmitter, Output, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { LoanApplicationService } from '../loan-application.service';
import { FieldErrors, LoanApplicationRequest, LoanApplicationResponse } from '../models';

@Component({
  selector: 'app-apply-form',
  imports: [ReactiveFormsModule],
  templateUrl: './apply-form.html',
})
export class ApplyForm {

  @Output() decided = new EventEmitter<LoanApplicationResponse>();   // tells the parent the API answered

  private readonly fb = inject(FormBuilder);
  private readonly loanService = inject(LoanApplicationService);

  readonly fields = [
    { name: 'applicantName', label: 'Applicant name', type: 'text' },
    { name: 'annualIncome', label: 'Annual income ($)', type: 'number' },
    { name: 'monthlyDebt', label: 'Monthly debt payments, incl. this loan ($)', type: 'number' },
    { name: 'loanAmount', label: 'Loan amount ($)', type: 'number' },
    { name: 'propertyValue', label: 'Property value ($)', type: 'number' },
    { name: 'creditScore', label: 'Credit score (300–850)', type: 'number' },
  ] as const;

  // Reactive form: the form model lives in TypeScript; client-side validators mirror the backend's
  readonly form = this.fb.group({
    applicantName: ['', Validators.required],
    annualIncome: [null as number | null, [Validators.required, Validators.min(1)]],
    monthlyDebt: [null as number | null, [Validators.required, Validators.min(0)]],
    loanAmount: [null as number | null, [Validators.required, Validators.min(1)]],
    propertyValue: [null as number | null, [Validators.required, Validators.min(1)]],
    creditScore: [null as number | null, [Validators.required, Validators.min(300), Validators.max(850)]],
  });

  serverErrors: FieldErrors = {};
  serverError: string | null = null;
  submitting = false;

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();   // show client-side errors, don't call the API
      return;
    }
    this.submitting = true;
    this.serverErrors = {};
    this.serverError = null;

    this.loanService.submit(this.form.getRawValue() as LoanApplicationRequest).subscribe({
      next: (result) => {
        this.submitting = false;
        this.decided.emit(result);
      },
      error: (err: HttpErrorResponse) => {
        this.submitting = false;
        if (err.status === 400) {
          this.serverErrors = err.error?.errors ?? {};   // backend is the final authority on validation
        } else {
          this.serverError = 'Something went wrong. Please try again.';
        }
      },
    });
  }

  // Client-side message for a field, if the user has touched it and it's invalid
  clientError(name: string): string | null {
    const control = this.form.get(name);
    if (!control || !control.touched || control.valid) return null;
    if (control.hasError('required')) return 'Required';
    if (control.hasError('min')) return `Must be at least ${control.errors?.['min'].min}`;
    if (control.hasError('max')) return `Must be at most ${control.errors?.['max'].max}`;
    return 'Invalid';
  }
}
