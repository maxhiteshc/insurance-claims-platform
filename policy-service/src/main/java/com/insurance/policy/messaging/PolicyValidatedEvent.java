package com.insurance.policy.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PolicyValidatedEvent(
        UUID eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        UUID claimId,
        UUID policyId,
        boolean covered,
        BigDecimal approvedAmount,
        String reason
) {
}
