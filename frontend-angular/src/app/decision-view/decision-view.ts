import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { LoanApplicationResponse } from '../models';

@Component({
  selector: 'app-decision-view',
  imports: [DecimalPipe],
  templateUrl: './decision-view.html',
})
export class DecisionView {

  @Input({ required: true }) result!: LoanApplicationResponse;   // data in from the parent
  @Output() newApplication = new EventEmitter<void>();            // event out to the parent

  readonly labels = { APPROVE: 'Approved', REFER: 'Referred to an underwriter', DECLINE: 'Declined' };
}
