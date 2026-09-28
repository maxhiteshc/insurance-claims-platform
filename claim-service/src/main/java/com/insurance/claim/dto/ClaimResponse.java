package com.insurance.claim.dto;

import com.insurance.claim.domain.Claim;
import com.insurance.claim.domain.ClaimStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ClaimResponse(
        UUID id,
        UUID policyId,
        UUID customerId,
        BigDecimal claimAmount,
        ClaimStatus status,
        Instant submittedAt,
        Instant updatedAt,
        Long version
) {

    public static ClaimResponse from(Claim claim) {
        return new ClaimResponse(
                claim.getId(),
                claim.getPolicyId(),
                claim.getCustomerId(),
                claim.getClaimAmount(),
                claim.getStatus(),
                claim.getSubmittedAt(),
                claim.getUpdatedAt(),
                claim.getVersion()
        );
    }
}