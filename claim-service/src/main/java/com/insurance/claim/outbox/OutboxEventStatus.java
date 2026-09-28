package com.insurance.claim.outbox;

public enum OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILED
}