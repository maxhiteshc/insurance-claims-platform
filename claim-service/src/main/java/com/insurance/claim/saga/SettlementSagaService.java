package com.insurance.claim.saga;

import com.insurance.claim.client.PaymentServiceClient;
import com.insurance.claim.client.PolicyServiceClient;
import com.insurance.claim.domain.Claim;
import com.insurance.claim.domain.ClaimStatus;
import com.insurance.claim.repository.ClaimRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SettlementSagaService {

    private final SettlementSagaRepository sagaRepository;
    private final ClaimRepository claimRepository;
    private final PolicyServiceClient policyServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public SettlementSagaService(SettlementSagaRepository sagaRepository,
                                 ClaimRepository claimRepository,
                                 PolicyServiceClient policyServiceClient,
                                 PaymentServiceClient paymentServiceClient) {
        this.sagaRepository = sagaRepository;
        this.claimRepository = claimRepository;
        this.policyServiceClient = policyServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }

    public void process(UUID sagaId) {
        SettlementSaga saga = sagaRepository.findById(sagaId).orElse(null);
        if (saga == null || isTerminal(saga.getStatus())) {
            return;
        }

        switch (saga.getStatus()) {
            case RESERVATION_PENDING -> reserveCoverage(saga);
            case PAYMENT_PENDING -> executePayment(saga);
            case COMPENSATION_PENDING -> compensate(saga);
            default -> { }
        }
    }

    private void reserveCoverage(SettlementSaga saga) {
        try {
            PolicyServiceClient.ReservationResponse response = policyServiceClient.reserve(
                    saga.getClaimId(), saga.getPolicyId(), saga.getCustomerId(), saga.getAmount());
            saga.markPaymentPending(response.reservationId());
            sagaRepository.save(saga);
            process(saga.getId());
        } catch (Exception exception) {
            saga.markFailed("Coverage reservation failed: " + safeMessage(exception));
            sagaRepository.save(saga);
            markClaimSettlementFailed(saga.getClaimId());
        }
    }

    private void executePayment(SettlementSaga saga) {
        try {
            PaymentServiceClient.PaymentResponse response = paymentServiceClient.execute(
                    saga.getClaimId(), saga.getCustomerId(), saga.getAmount());

            if ("SUCCESS".equalsIgnoreCase(response.status())) {
                markClaimSettled(saga.getClaimId());
                saga.markCompleted(response.transactionReference() != null
                        ? response.transactionReference()
                        : response.id() == null ? null : response.id().toString());
                sagaRepository.save(saga);
                return;
            }

            saga.markCompensationPending(
                    response.failureReason() == null ? "Payment failed" : response.failureReason());
            sagaRepository.save(saga);
            process(saga.getId());
        } catch (Exception exception) {
            saga.scheduleRetryWithError("Payment execution failed: " + safeMessage(exception));
            sagaRepository.save(saga);
        }
    }

    private void compensate(SettlementSaga saga) {
        if (saga.getReservationId() == null) {
            markClaimSettlementFailed(saga.getClaimId());
            saga.markCompensated();
            sagaRepository.save(saga);
            return;
        }

        try {
            policyServiceClient.release(saga.getClaimId(), saga.getReservationId());
            markClaimSettlementFailed(saga.getClaimId());
            saga.markCompensated();
            sagaRepository.save(saga);
        } catch (Exception exception) {
            saga.scheduleRetryWithError("Compensation failed: " + safeMessage(exception));
            sagaRepository.save(saga);
        }
    }

    @Scheduled(fixedDelayString = "${settlement.saga.recovery-delay-ms:5000}")
    public void recoverPendingSagas() {
        List<SettlementSaga> pending = sagaRepository
                .findTop20ByStatusInAndNextAttemptAtLessThanEqualOrderByNextAttemptAtAsc(
                        List.of(
                                SettlementSagaStatus.RESERVATION_PENDING,
                                SettlementSagaStatus.PAYMENT_PENDING,
                                SettlementSagaStatus.COMPENSATION_PENDING),
                        Instant.now(),
                        org.springframework.data.domain.PageRequest.of(0, 20));

        pending.forEach(saga -> process(saga.getId()));
    }

    private void markClaimSettled(UUID claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalStateException("Claim not found: " + claimId));
        claim.updateStatus(ClaimStatus.SETTLED);
        claimRepository.save(claim);
    }

    private void markClaimSettlementFailed(UUID claimId) {
        Claim claim = claimRepository.findById(claimId).orElse(null);
        if (claim != null && claim.getStatus() != ClaimStatus.SETTLED) {
            claim.updateStatus(ClaimStatus.SETTLEMENT_FAILED);
            claimRepository.save(claim);
        }
    }

    private boolean isTerminal(SettlementSagaStatus status) {
        return status == SettlementSagaStatus.COMPLETED
                || status == SettlementSagaStatus.COMPENSATED
                || status == SettlementSagaStatus.FAILED;
    }

    private String safeMessage(Exception exception) {
        String message = exception.getMessage();
        return message == null ? exception.getClass().getSimpleName() : message.substring(0, Math.min(message.length(), 900));
    }
}
