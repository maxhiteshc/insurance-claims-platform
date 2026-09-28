package com.insurance.policy.dto;
import java.math.BigDecimal;
import java.util.UUID;
public record ReservationResponse(String reservationId,UUID claimId,UUID policyId,BigDecimal amount,String status) {}
