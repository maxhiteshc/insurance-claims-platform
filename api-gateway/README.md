# API Gateway

Spring Cloud Gateway edge service for the insurance claims platform.

## Responsibilities

- Route requests to claim, policy and payment services.
- Validate JWTs at the edge.
- Generate/propagate `X-Correlation-Id`.
- Keep actuator health endpoints public.

Service-level JWT validation is still required in each business service; gateway validation is not the only security boundary.

## Local defaults

- Gateway: `8080`
- Claim service: `8081`
- Policy service: `8082`
- Payment service: `8083`
- Keycloak issuer: `http://localhost:8080/realms/insurance`

Override service URLs and the Keycloak issuer with environment variables when running in Docker.

## Note

Rate limiting and circuit-breaker fallback will be added after the initial gateway/security compilation slice, together with the Keycloak Docker configuration.
