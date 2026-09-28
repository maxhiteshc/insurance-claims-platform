# Kafka infrastructure overlay

## Exact location

Extract this ZIP directly into:

`C:\Users\vicky\IdeaProjects\insurance-claims-platform\`

It does NOT replace the existing docker-compose.yml.

## Compose change

Open:

`infrastructure/docker/kafka/docker-compose-kafka.yml.txt`

Copy the `kafka:` service into the existing
`infrastructure/docker/docker-compose.yml`.

Then add:

```yaml
volumes:
  oracle-data:
  kafka-data:
```

under the existing top-level `volumes:` section.

The development broker exposes `localhost:9092` for applications running
on the Windows host. Containers in the same Compose network can use
`kafka:9092`.

## Topics

The application already uses:
- `claims.events`
- `policy.events`

The DLT names reserved for the next retry/DLT implementation are:
- `claims.events.DLT`
- `policy.events.DLT`

## Important

Do not run Docker yet if Docker Desktop is still unhealthy.

After the Compose file is updated, validate the YAML without starting
containers:

```powershell
docker compose -f .\infrastructure\docker\docker-compose.yml config
```

When Docker is healthy later, we will create/verify the topics and test the
full event flow.

## Why one Kafka broker?

This is a local assignment environment. A single broker keeps the stack small
while still demonstrating Kafka choreography, retries, idempotency and DLT
behavior. Production would use multiple brokers and replication.
