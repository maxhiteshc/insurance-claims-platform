package com.insurance.claim.audit;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "claim_event_audit", indexes = {
        @Index(name = "idx_claim_audit_claim_id", columnList = "claim_id"),
        @Index(name = "idx_claim_audit_event_id", columnList = "event_id")
})
public class ClaimEventAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "claim_id", nullable = false)
    private UUID claimId;

    @Column(name = "event_id", nullable = false, length = 36)
    private String eventId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "event_version", nullable = false)
    private Integer eventVersion;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    @Column(name = "outcome", nullable = false, length = 50)
    private String outcome;

    @Column(name = "reason", length = 500)
    private String reason;

    protected ClaimEventAudit() {}

    public ClaimEventAudit(UUID claimId, String eventId, String eventType, int eventVersion,
                           Instant occurredAt, String outcome, String reason) {
        this.claimId = claimId;
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventVersion = eventVersion;
        this.occurredAt = occurredAt;
        this.processedAt = Instant.now();
        this.outcome = outcome;
        this.reason = reason;
    }
}
