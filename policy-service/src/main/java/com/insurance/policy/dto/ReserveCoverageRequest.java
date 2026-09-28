package com.insurance.policy.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
public record ReserveCoverageRequest(@NotNull UUID claimId,@NotNull UUID customerId,@NotNull @DecimalMin("0.01") BigDecimal amount) {}
