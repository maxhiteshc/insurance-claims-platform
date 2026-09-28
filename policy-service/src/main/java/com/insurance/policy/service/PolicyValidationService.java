package com.insurance.policy.service;

import com.insurance.policy.domain.Policy;
import com.insurance.policy.messaging.ClaimSubmittedEvent;
import com.insurance.policy.messaging.PolicyValidatedEvent;
import com.insurance.policy.outbox.OutboxEvent;
import com.insurance.policy.outbox.OutboxEventRepository;
import com.insurance.policy.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class PolicyValidationService {

    private final PolicyRepository policyRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final JsonMapper jsonMapper;

    public PolicyValidationService(
            PolicyRepository policyRepository,
            OutboxEventRepository outboxEventRepository,
            JsonMapper jsonMapper
    ) {
        this.policyRepository = policyRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.jsonMapper = jsonMapper;
    }

    @Transactional
    public void validate(ClaimSubmittedEvent event) {
        PolicyValidatedEvent response = validatePolicy(event);

        try {
            String payload = jsonMapper.writeValueAsString(response);
            outboxEventRepository.save(new OutboxEvent(
                    "Claim",
                    event.claimId(),
                    response.eventType(),
                    payload
            ));
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to create PolicyValidated outbox event",
                    exception
            );
        }
    }

    private PolicyValidatedEvent validatePolicy(ClaimSubmittedEvent event) {
        Policy policy = policyRepository.findById(event.policyId()).orElse(null);

        if (policy == null) {
            return response(event, false, BigDecimal.ZERO, "POLICY_NOT_FOUND");
        }
        if (!policy.getCustomerId().equals(event.customerId())) {
            return response(event, false, BigDecimal.ZERO, "CUSTOMER_POLICY_MISMATCH");
        }
        if (!policy.isActiveOn(LocalDate.now())) {
            return response(event, false, BigDecimal.ZERO, "POLICY_NOT_ACTIVE");
        }
        if (event.claimAmount().compareTo(policy.getCoverageLimit()) > 0) {
            return response(event, false, BigDecimal.ZERO, "COVERAGE_LIMIT_EXCEEDED");
        }

        return response(event, true, event.claimAmount(), "POLICY_COVERAGE_VALID");
    }

    private PolicyValidatedEvent response(
            ClaimSubmittedEvent event,
            boolean covered,
            BigDecimal approvedAmount,
            String reason
    ) {
        return new PolicyValidatedEvent(
                UUID.randomUUID(),
                covered ? "PolicyValidated" : "PolicyRejected",
                1,
                Instant.now(),
                event.claimId(),
                event.policyId(),
                covered,
                approvedAmount,
                reason
        );
    }
}
