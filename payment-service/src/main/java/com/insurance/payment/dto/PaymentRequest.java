package com.insurance.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
        @NotNull UUID claimId,
        @NotNull UUID customerId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount
) {
}
