package com.insurance.claim.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PolicyValidationEvent(
        UUID eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        UUID claimId,
        UUID policyId,
        boolean covered,
        BigDecimal approvedAmount,
        String reason
) {}
