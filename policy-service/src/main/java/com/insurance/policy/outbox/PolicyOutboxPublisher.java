package com.insurance.policy.outbox;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PolicyOutboxPublisher {

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    private final int batchSize;
    private final int maxRetries;

    public PolicyOutboxPublisher(
            OutboxEventRepository repository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${kafka.topics.policy-validation:policy.events}") String topic,
            @Value("${outbox.publisher.batch-size:20}") int batchSize,
            @Value("${outbox.publisher.max-retries:5}") int maxRetries
    ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.batchSize = batchSize;
        this.maxRetries = maxRetries;
    }

    @Scheduled(fixedDelayString = "${outbox.publisher.fixed-delay-ms:1000}")
    public void publishPendingEvents() {
        var events = repository
                .findByStatusAndNextAttemptAtLessThanEqual(
                        OutboxEventStatus.PENDING,
                        Instant.now(),
                        PageRequest.of(0, batchSize)
                )
                .getContent();

        events.forEach(this::publish);
    }

    private void publish(OutboxEvent event) {
        try {
            kafkaTemplate
                    .send(topic, event.getAggregateId().toString(), event.getPayload())
                    .get();

            event.markPublished();
            repository.save(event);

        } catch (Exception exception) {
            if (event.getRetryCount() + 1 >= maxRetries) {
                event.markFailed();
            } else {
                long delaySeconds = Math.min(
                        60,
                        (long) Math.pow(2, event.getRetryCount())
                );
                event.scheduleRetry(Instant.now().plusSeconds(delaySeconds));
            }
            repository.save(event);
        }
    }
}
