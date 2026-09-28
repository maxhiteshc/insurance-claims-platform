package com.insurance.claim.saga;

public enum SettlementSagaStatus {
    RESERVATION_PENDING,
    PAYMENT_PENDING,
    COMPENSATION_PENDING,
    COMPLETED,
    COMPENSATED,
    FAILED
}
