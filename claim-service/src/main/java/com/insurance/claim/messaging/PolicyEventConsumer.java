package com.insurance.claim.messaging;

import com.insurance.claim.audit.ClaimEventAudit;
import com.insurance.claim.audit.ClaimEventAuditRepository;
import com.insurance.claim.domain.Claim;
import com.insurance.claim.domain.ClaimStatus;
import com.insurance.claim.event.ProcessedEvent;
import com.insurance.claim.event.ProcessedEventRepository;
import com.insurance.claim.repository.ClaimRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Component
public class PolicyEventConsumer {
    private final ClaimRepository claimRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ClaimEventAuditRepository auditRepository;
    private final JsonMapper jsonMapper;

    public PolicyEventConsumer(ClaimRepository claimRepository,
                               ProcessedEventRepository processedEventRepository,
                               ClaimEventAuditRepository auditRepository,
                               JsonMapper jsonMapper) {
        this.claimRepository = claimRepository;
        this.processedEventRepository = processedEventRepository;
        this.auditRepository = auditRepository;
        this.jsonMapper = jsonMapper;
    }

    @KafkaListener(
            topics = "${kafka.topics.policy-validation:policy.events}",
            groupId = "${spring.kafka.consumer.group-id:claim-service}")
    @Transactional
    public void consume(String payload) {
        try {
            PolicyValidationEvent event = jsonMapper.readValue(payload, PolicyValidationEvent.class);

            if (processedEventRepository.existsById(event.eventId().toString())) {
                return;
            }

            Claim claim = claimRepository.findById(event.claimId()).orElse(null);
            if (claim == null) {
                auditRepository.save(new ClaimEventAudit(
                        event.claimId(), event.eventId().toString(), event.eventType(),
                        event.eventVersion(), event.occurredAt(), "CLAIM_NOT_FOUND", event.reason()));
                processedEventRepository.save(new ProcessedEvent(event.eventId().toString(), event.eventType()));
                return;
            }

            if ("PolicyValidated".equals(event.eventType()) && event.covered()) {
                if (claim.getStatus() == ClaimStatus.SUBMITTED) {
                    claim.updateStatus(ClaimStatus.UNDER_REVIEW);
                    claimRepository.save(claim);
                    auditRepository.save(new ClaimEventAudit(
                            event.claimId(), event.eventId().toString(), event.eventType(),
                            event.eventVersion(), event.occurredAt(), "CLAIM_MOVED_TO_UNDER_REVIEW", event.reason()));
                } else {
                    auditRepository.save(new ClaimEventAudit(
                            event.claimId(), event.eventId().toString(), event.eventType(),
                            event.eventVersion(), event.occurredAt(), "STALE_OR_OUT_OF_ORDER", event.reason()));
                }
            } else if ("PolicyRejected".equals(event.eventType()) || !event.covered()) {
                if (claim.getStatus() == ClaimStatus.SUBMITTED) {
                    claim.updateStatus(ClaimStatus.REJECTED);
                    claimRepository.save(claim);
                    auditRepository.save(new ClaimEventAudit(
                            event.claimId(), event.eventId().toString(), event.eventType(),
                            event.eventVersion(), event.occurredAt(), "CLAIM_REJECTED", event.reason()));
                } else {
                    auditRepository.save(new ClaimEventAudit(
                            event.claimId(), event.eventId().toString(), event.eventType(),
                            event.eventVersion(), event.occurredAt(), "STALE_OR_OUT_OF_ORDER", event.reason()));
                }
            } else {
                auditRepository.save(new ClaimEventAudit(
                        event.claimId(), event.eventId().toString(), event.eventType(),
                        event.eventVersion(), event.occurredAt(), "UNSUPPORTED_EVENT", event.reason()));
            }

            processedEventRepository.save(new ProcessedEvent(event.eventId().toString(), event.eventType()));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to process policy event", exception);
        }
    }
}
