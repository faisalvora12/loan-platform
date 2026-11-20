# Loan Platform

A small loan application processing system. A client submits a loan application, the API runs it
through a set of underwriting rules, and returns a decision (approve, refer or decline) with the
reason for each rule.

## Stack

- **API:** Java 17, Spring Boot 3.5 (Web, Validation, Data JPA, Data MongoDB), Maven
- **Databases:** PostgreSQL for loan applications, MongoDB for the decision audit log
- **Front ends:** the same two screens built twice, once in React (Vite, TypeScript) and once in Angular
- **Tests:** JUnit 5, Mockito, Spring MockMvc
- **Run:** Docker Compose

## How it works

1. `POST /api/applications` validates the request (name, income, monthly debt, loan amount,
   property value, credit score).
2. Each underwriting rule gives its own verdict and reason:

   | Rule | Approve | Refer | Decline |
   |---|---|---|---|
   | Debt-to-income (DTI) | up to 36% | 36% to 43% | above 43% |
   | Loan-to-value (LTV) | up to 80% | 80% to 95% | above 95% |
   | Credit score | 680 or higher | 620 to 679 | below 620 |

3. The worst verdict wins: one decline declines the application, one refer sends it to a person.
4. The application and its reasons are saved to Postgres, and a snapshot of the inputs and the
   decision is written to MongoDB as an audit record. If the audit write fails, the Postgres
   transaction rolls back, so there is no decision without an audit record.

Each rule is its own class behind one interface, so adding a rule means adding a class; the
service that combines the verdicts does not change.

## API

| Method | Path | What it does |
|---|---|---|
| POST | `/api/applications` | Submit an application; returns 201 with the decision |
| GET | `/api/applications/{id}` | Get one application; 404 if it does not exist |
| GET | `/api/applications?decision=REFER` | List applications by decision |

Example:

```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -d '{"applicantName":"Jane Doe","annualIncome":120000,"monthlyDebt":3000,
       "loanAmount":320000,"propertyValue":400000,"creditScore":720}'
```

## Run it

API, Postgres and MongoDB:

```bash
docker compose up --build
```

The API is then on http://localhost:8080.

React front end (http://localhost:5173):

```bash
cd frontend && npm install && npm run dev
```

Angular front end (http://localhost:4200):

```bash
cd frontend-angular && npm install && npm start
```

## Tests

```bash
mvn test
```

The tests cover the three rules at their boundaries, how verdicts are combined, and the
controller's status codes and validation errors. They do not need a database.

## Layout

```
src/main/java/com/loanplatform/
  application/    REST controller, service, JPA entity and repository, request/response records
  underwriting/   the rule interface, the three rules, and the service that combines them
  audit/          the MongoDB audit document and repository
  common/         CORS config and error handling
frontend/         React front end
frontend-angular/ Angular front end
```
