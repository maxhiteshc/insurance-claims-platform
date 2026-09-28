## ⚠️ Evaluation & Proprietary Notice

This repository and its contents are **strictly provided for evaluation purposes only**.

**Copyright © 2026 Hitesh Chhabria. All rights reserved.**

This project, including its source code, architecture, documentation, configuration, diagrams, and other associated materials, is proprietary to **Hitesh Chhabria**.

No permission is granted to copy, reproduce, modify, distribute, publish, sublicense, commercially use, or create derivative works from this repository or any part of it without prior written permission from the copyright owner.

This repository has been created and shared solely for the purpose of **technical evaluation and assessment**.

By accessing or reviewing this repository, you acknowledge that the contents remain the intellectual property of Hitesh Chhabria and may not be reused for purposes other than evaluation without explicit written authorization.

### License

**Proprietary — All Rights Reserved**

This repository is **not open source** and is not licensed under MIT, Apache 2.0, GPL, or any other open-source license.



# API Gateway Circuit Breaker Overlay

This overlay adds the gateway fallback controller and provides the exact
configuration block to merge into the existing `application.yaml`.

## Important: merge, don't replace

Do NOT replace the existing `api-gateway/src/main/resources/application.yaml`.

Copy the contents of:

`api-gateway/src/main/resources/application-resilience.yaml`

into the existing `application.yaml`, merging with the current
`spring.cloud.gateway.routes` section if it already exists.

The three routes use environment variables:

- `CLAIM_SERVICE_URL` (default `http://localhost:8081`)
- `POLICY_SERVICE_URL` (default `http://localhost:8082`)
- `PAYMENT_SERVICE_URL` (default `http://localhost:8083`)

Circuit breakers:
- count-based window: 10 calls
- minimum calls: 5
- open at 50% failures
- open state: 10 seconds
- half-open probes: 2
- request timeout: 3 seconds

Fallback responses intentionally do not expose stack traces or internal
exception details.

After merging, compile with:

```powershell
.\claim-service\mvnw.cmd -f .\api-gateway\pom.xml clean compile -DskipTests
```

Redis rate limiting is intentionally not included in this overlay. It will be
added after the final infrastructure decision.
