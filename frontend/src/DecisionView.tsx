import type { LoanApplicationResponse } from './types';

interface Props {
  result: LoanApplicationResponse;
  onNew: () => void;
}

const LABEL = { APPROVE: 'Approved', REFER: 'Referred to an underwriter', DECLINE: 'Declined' };

export function DecisionView({ result, onNew }: Props) {
  return (
    <section>
      <h1>Decision</h1>
      <p className={`badge ${result.decision.toLowerCase()}`}>{LABEL[result.decision]}</p>
      <p>
        Application #{result.id} for {result.applicantName}, loan $
        {Number(result.loanAmount).toLocaleString()}
      </p>
      <h2>Reasons</h2>
      <ul>
        {result.reasons.map((reason) => (
          <li key={reason}>{reason}</li>
        ))}
      </ul>
      <button onClick={onNew}>New application</button>
    </section>
  );
}
