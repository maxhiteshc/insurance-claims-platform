# Payment Service

Simulated payment service for the Insurance Claims Processing Platform.

## Responsibility

- Execute a simulated claim settlement payment.
- Return `SUCCESS` or a configurable simulated `FAILED` result.
- Persist payment state in the payment service's Oracle schema.
- Provide idempotent execution by `claimId`: repeated requests for the same claim return the existing payment rather than creating another payment.
- Expose a payment lookup endpoint for the settlement Saga.

## Failure simulation

`PAYMENT_FAILURE_RATE` controls the probability of a simulated payment failure:

- `0.0` = always succeed
- `1.0` = always fail
- `0.2` = approximately 20% failure rate

Example:

```powershell
$env:PAYMENT_FAILURE_RATE="1.0"
```

This will be useful later when demonstrating Saga compensation.

## Endpoints

`POST /api/v1/payments`

```json
{
  "claimId": "...",
  "customerId": "...",
  "amount": 12500.00
}
```

`GET /api/v1/payments/claim/{claimId}`

## Persistence

The service uses the `PAYMENT_USER` Oracle schema and Flyway migrations. `@Version` provides optimistic locking on the payment aggregate.

## Assignment mapping

The service implements the assignment's simulated payment responsibility and provides a deterministic failure mode for demonstrating settlement compensation. The settlement Saga itself remains owned by `claim-service`.
