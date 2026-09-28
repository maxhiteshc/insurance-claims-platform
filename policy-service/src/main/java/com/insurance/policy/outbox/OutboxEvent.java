package com.insurance.policy.outbox;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "outbox_events",
        indexes = {
                @Index(name = "idx_policy_outbox_status_created", columnList = "status, created_at")
        }
)
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxEventStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    protected OutboxEvent() {
    }

    public OutboxEvent(String aggregateType, UUID aggregateId, String eventType, String payload) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = OutboxEventStatus.PENDING;
        this.createdAt = Instant.now();
        this.retryCount = 0;
        this.nextAttemptAt = this.createdAt;
    }

    public UUID getId() { return id; }
    public UUID getAggregateId() { return aggregateId; }
    public String getPayload() { return payload; }
    public Integer getRetryCount() { return retryCount; }

    public void markPublished() {
        status = OutboxEventStatus.PUBLISHED;
        publishedAt = Instant.now();
    }

    public void scheduleRetry(Instant nextAttemptAt) {
        retryCount++;
        this.nextAttemptAt = nextAttemptAt;
        status = OutboxEventStatus.PENDING;
    }

    public void markFailed() {
        retryCount++;
        status = OutboxEventStatus.FAILED;
        nextAttemptAt = null;
    }
}
