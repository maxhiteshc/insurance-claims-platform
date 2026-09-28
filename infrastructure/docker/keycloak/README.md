# Keycloak runtime configuration

This directory contains the development Keycloak realm used by the insurance claims platform.

Realm:
- `insurance`

Realm roles:
- `CUSTOMER`
- `ADJUSTER`
- `ADMIN`

Clients:
- `insurance-gateway` — public OIDC client with Authorization Code + PKCE
- `claim-service` — confidential/service-account client
- `policy-service` — confidential/service-account client
- `payment-service` — confidential/service-account client

## Docker Compose integration

Add the following service to the existing
`infrastructure/docker/docker-compose.yml` rather than replacing the existing file:

```yaml
  keycloak:
    image: quay.io/keycloak/keycloak:26.3
    container_name: insurance-keycloak
    command: start-dev --import-realm
    environment:
      KC_BOOTSTRAP_ADMIN_USERNAME: admin
      KC_BOOTSTRAP_ADMIN_PASSWORD: admin
    ports:
      - "8088:8080"
    volumes:
      - ./keycloak/realm-export.json:/opt/keycloak/data/import/insurance-realm.json:ro
    healthcheck:
      test: ["CMD-SHELL", "bash -c 'exec 3<>/dev/tcp/127.0.0.1/8080'"]
      interval: 10s
      timeout: 5s
      retries: 20
      start_period: 30s
```

The realm import is intended for local development only. Do not use these
development credentials in a production environment.

## Issuer

Local issuer:

`http://localhost:8088/realms/insurance`

The gateway and business services should use the issuer configured through
environment variables rather than hard-coding production URLs.
