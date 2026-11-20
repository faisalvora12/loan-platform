import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoanApplicationRequest, LoanApplicationResponse } from './models';

// All HTTP calls live in a service, not in components: one place to change URLs, add headers, or mock in tests
@Injectable({ providedIn: 'root' })   // one shared instance for the whole app
export class LoanApplicationService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/applications';

  // Returns an Observable: nothing is sent until a component subscribes
  submit(request: LoanApplicationRequest): Observable<LoanApplicationResponse> {
    return this.http.post<LoanApplicationResponse>(this.apiUrl, request);
  }

  get(id: number): Observable<LoanApplicationResponse> {
    return this.http.get<LoanApplicationResponse>(`${this.apiUrl}/${id}`);
  }
}
