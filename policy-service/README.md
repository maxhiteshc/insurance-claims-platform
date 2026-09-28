# Policy Service

Insurance Claims Platform policy service.

Responsibilities:
- Own policy data in the POLICY_USER Oracle schema.
- Consume ClaimSubmitted events from claims.events.
- Validate policy existence, customer ownership, active dates and coverage limit.
- Prevent duplicate processing using processed_events.
- Persist PolicyValidated events through an outbox.
- Publish PolicyValidated events to policy.events with retry/backoff.

Port: 8082

Database:
jdbc:oracle:thin:@localhost:1521/FREEPDB1
User: POLICY_USER

Consumes: claims.events
Produces: policy.events
