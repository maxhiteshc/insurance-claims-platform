package com.insurance.payment.dto;

import com.insurance.payment.domain.Payment;
import com.insurance.payment.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID claimId,
        UUID customerId,
        BigDecimal amount,
        PaymentStatus status,
        String transactionReference,
        String failureReason,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getClaimId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getTransactionReference(),
                payment.getFailureReason(),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getVersion()
        );
    }
}
