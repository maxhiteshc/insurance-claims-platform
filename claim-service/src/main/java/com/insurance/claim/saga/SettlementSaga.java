package com.insurance.claim.saga;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "settlement_sagas", indexes = {
        @Index(name = "idx_saga_status_next_attempt", columnList = "status,next_attempt_at"),
        @Index(name = "idx_saga_claim_id", columnList = "claim_id", unique = true)
})
public class SettlementSaga {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "claim_id", nullable = false, unique = true)
    private UUID claimId;

    @Column(name = "policy_id", nullable = false)
    private UUID policyId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private SettlementSagaStatus status;

    @Column(name = "reservation_id", length = 100)
    private String reservationId;

    @Column(name = "payment_id", length = 100)
    private String paymentId;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    protected SettlementSaga() {
    }

    public SettlementSaga(UUID claimId, UUID policyId, UUID customerId, BigDecimal amount) {
        this.claimId = claimId;
        this.policyId = policyId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = SettlementSagaStatus.RESERVATION_PENDING;
        this.retryCount = 0;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.nextAttemptAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getClaimId() { return claimId; }
    public UUID getPolicyId() { return policyId; }
    public UUID getCustomerId() { return customerId; }
    public BigDecimal getAmount() { return amount; }
    public SettlementSagaStatus getStatus() { return status; }
    public String getReservationId() { return reservationId; }
    public String getPaymentId() { return paymentId; }
    public Integer getRetryCount() { return retryCount; }
    public String getLastError() { return lastError; }
    public Instant getNextAttemptAt() { return nextAttemptAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    public void markPaymentPending(String reservationId) {
        this.reservationId = reservationId;
        this.status = SettlementSagaStatus.PAYMENT_PENDING;
        clearError();
    }

    public void markCompleted(String paymentId) {
        this.paymentId = paymentId;
        this.status = SettlementSagaStatus.COMPLETED;
        clearError();
    }

    public void markCompensationPending(String error) {
        this.status = SettlementSagaStatus.COMPENSATION_PENDING;
        this.lastError = error;
        scheduleRetry();
    }

    public void markCompensated() {
        this.status = SettlementSagaStatus.COMPENSATED;
        clearError();
    }

    public void markFailed(String error) {
        this.status = SettlementSagaStatus.FAILED;
        this.lastError = error;
    }

    public void scheduleRetryWithError(String error) {
        this.retryCount++;
        this.lastError = error;
        long delaySeconds = Math.min(60L, 1L << Math.min(retryCount, 6));
        this.nextAttemptAt = Instant.now().plusSeconds(delaySeconds);
    }

    private void scheduleRetry() {
        this.retryCount++;
        long delaySeconds = Math.min(60L, 1L << Math.min(retryCount, 6));
        this.nextAttemptAt = Instant.now().plusSeconds(delaySeconds);
    }

    private void clearError() {
        this.lastError = null;
        this.nextAttemptAt = Instant.now();
    }
}
