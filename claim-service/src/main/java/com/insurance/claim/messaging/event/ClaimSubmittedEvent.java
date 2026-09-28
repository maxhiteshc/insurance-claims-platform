package com.insurance.claim.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ClaimSubmittedEvent(
        UUID eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        UUID claimId,
        UUID policyId,
        UUID customerId,
        BigDecimal claimAmount
) {
}