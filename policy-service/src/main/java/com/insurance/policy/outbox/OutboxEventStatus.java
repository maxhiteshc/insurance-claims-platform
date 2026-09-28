package com.insurance.policy.outbox;

public enum OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
