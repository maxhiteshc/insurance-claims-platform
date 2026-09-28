package com.insurance.claim.outbox;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    Page<OutboxEvent> findByStatusAndNextAttemptAtLessThanEqual(
            OutboxEventStatus status,
            Instant now,
            Pageable pageable
    );
}