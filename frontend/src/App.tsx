import { useState } from 'react';
import { ApplyForm } from './ApplyForm';
import { DecisionView } from './DecisionView';
import type { LoanApplicationResponse } from './types';
import './App.css';

// Two screens: the form, or the decision once the API has answered
function App() {
  const [result, setResult] = useState<LoanApplicationResponse | null>(null);

  return (
    <main>
      {result
        ? <DecisionView result={result} onNew={() => setResult(null)} />
        : <ApplyForm onDecision={setResult} />}
    </main>
  );
}

export default App;
