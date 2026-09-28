package com.insurance.policy.messaging;

import com.insurance.policy.idempotency.ProcessedEvent;
import com.insurance.policy.idempotency.ProcessedEventRepository;
import com.insurance.policy.service.PolicyValidationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Component
public class ClaimSubmittedConsumer {

    private final JsonMapper jsonMapper;
    private final ProcessedEventRepository processedEventRepository;
    private final PolicyValidationService policyValidationService;

    public ClaimSubmittedConsumer(
            JsonMapper jsonMapper,
            ProcessedEventRepository processedEventRepository,
            PolicyValidationService policyValidationService
    ) {
        this.jsonMapper = jsonMapper;
        this.processedEventRepository = processedEventRepository;
        this.policyValidationService = policyValidationService;
    }

    @KafkaListener(
            topics = "${kafka.topics.claims:claims.events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void consume(String payload) {
        try {
            ClaimSubmittedEvent event =
                    jsonMapper.readValue(payload, ClaimSubmittedEvent.class);

            if (processedEventRepository.existsById(event.eventId().toString())) {
                return;
            }

            policyValidationService.validate(event);

            processedEventRepository.save(
                    new ProcessedEvent(
                            event.eventId().toString(),
                            event.eventType()
                    )
            );
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to process ClaimSubmitted event",
                    exception
            );
        }
    }
}
