import { Component, signal } from '@angular/core';
import { ApplyForm } from './apply-form/apply-form';
import { DecisionView } from './decision-view/decision-view';
import { LoanApplicationResponse } from './models';

// Two screens: the form, or the decision once the API has answered
@Component({
  selector: 'app-root',
  imports: [ApplyForm, DecisionView],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly result = signal<LoanApplicationResponse | null>(null);
}
