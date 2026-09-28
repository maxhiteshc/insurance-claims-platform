package com.insurance.claim.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "claims",
        indexes = {
                @Index(name = "idx_claim_policy_id", columnList = "policy_id"),
                @Index(name = "idx_claim_customer_id", columnList = "customer_id"),
                @Index(name = "idx_claim_status", columnList = "status")
        }
)
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "policy_id", nullable = false)
    private UUID policyId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "claim_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal claimAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ClaimStatus status;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    protected Claim() {
    }

    public Claim(
            UUID policyId,
            UUID customerId,
            BigDecimal claimAmount
    ) {
        this.policyId = policyId;
        this.customerId = customerId;
        this.claimAmount = claimAmount;
        this.status = ClaimStatus.SUBMITTED;
        this.submittedAt = Instant.now();
        this.updatedAt = this.submittedAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void updateStatus(ClaimStatus status) {
        this.status = status;
    }
}