package com.insurance.claim.service;

import com.insurance.claim.domain.Claim;
import com.insurance.claim.domain.ClaimStatus;
import com.insurance.claim.dto.ClaimResponse;
import com.insurance.claim.dto.CreateClaimRequest;
import com.insurance.claim.messaging.event.ClaimSubmittedEvent;
import com.insurance.claim.outbox.OutboxEvent;
import com.insurance.claim.outbox.OutboxEventRepository;
import com.insurance.claim.repository.ClaimRepository;
import com.insurance.claim.saga.SettlementSaga;
import com.insurance.claim.saga.SettlementSagaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.UUID;

@Service
public class ClaimService {
    private final ClaimRepository claimRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final SettlementSagaRepository settlementSagaRepository;
    private final JsonMapper jsonMapper;

    public ClaimService(ClaimRepository claimRepository, OutboxEventRepository outboxEventRepository,
                        SettlementSagaRepository settlementSagaRepository, JsonMapper jsonMapper) {
        this.claimRepository = claimRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.settlementSagaRepository = settlementSagaRepository;
        this.jsonMapper = jsonMapper;
    }

    @Transactional
    public ClaimResponse createClaim(CreateClaimRequest request) {
        Claim claim = new Claim(request.policyId(), request.customerId(), request.claimAmount());
        Claim savedClaim = claimRepository.save(claim);
        ClaimSubmittedEvent event = new ClaimSubmittedEvent(UUID.randomUUID(), "ClaimSubmitted", 1, Instant.now(),
                savedClaim.getId(), savedClaim.getPolicyId(), savedClaim.getCustomerId(), savedClaim.getClaimAmount());
        try {
            String payload = jsonMapper.writeValueAsString(event);
            outboxEventRepository.save(new OutboxEvent("Claim", savedClaim.getId(), event.eventType(), payload));
        } catch (JacksonException exception) {
            throw new IllegalStateException("Unable to serialize ClaimSubmitted event", exception);
        }
        return ClaimResponse.from(savedClaim);
    }

    @Transactional
    public UUID approveClaim(UUID claimId) {
        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new ClaimNotFoundException(claimId));
        if (claim.getStatus() != ClaimStatus.UNDER_REVIEW) {
            throw new InvalidClaimStateException("Claim " + claimId + " must be UNDER_REVIEW before approval. Current state: " + claim.getStatus());
        }
        claim.updateStatus(ClaimStatus.APPROVED);
        SettlementSaga saga = settlementSagaRepository.findByClaimId(claimId)
                .orElseGet(() -> settlementSagaRepository.save(new SettlementSaga(
                        claim.getId(), claim.getPolicyId(), claim.getCustomerId(), claim.getClaimAmount())));
        claim.updateStatus(ClaimStatus.SETTLEMENT_PENDING);
        claimRepository.save(claim);
        return saga.getId();
    }

    public static class ClaimNotFoundException extends RuntimeException {
        public ClaimNotFoundException(UUID claimId) { super("Claim not found: " + claimId); }
    }
    public static class InvalidClaimStateException extends RuntimeException {
        public InvalidClaimStateException(String message) { super(message); }
    }
}
