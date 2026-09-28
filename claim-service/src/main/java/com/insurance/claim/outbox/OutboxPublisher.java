package com.insurance.claim.outbox;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String topic;
    private final int batchSize;
    private final int maxRetries;

    public OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${outbox.publisher.topic}") String topic,
            @Value("${outbox.publisher.batch-size:20}") int batchSize,
            @Value("${outbox.publisher.max-retries:5}") int maxRetries
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.batchSize = batchSize;
        this.maxRetries = maxRetries;
    }

    @Scheduled(
            fixedDelayString = "${outbox.publisher.fixed-delay-ms:1000}"
    )
    public void publishPendingEvents() {

        List<OutboxEvent> events = outboxEventRepository
                .findByStatusAndNextAttemptAtLessThanEqual(
                        OutboxEventStatus.PENDING,
                        Instant.now(),
                        org.springframework.data.domain.PageRequest.of(
                                0,
                                batchSize
                        )
                )
                .getContent();

        for (OutboxEvent event : events) {
            publish(event);
        }
    }

    private void publish(OutboxEvent event) {

        try {
            kafkaTemplate
                    .send(
                            topic,
                            event.getAggregateId().toString(),
                            event.getPayload()
                    )
                    .get();

            event.markPublished();
            outboxEventRepository.save(event);

        } catch (Exception exception) {

            int currentRetryCount = event.getRetryCount();

            if (currentRetryCount + 1 >= maxRetries) {
                event.markFailed(null);
            } else {
                event.scheduleRetry(
                        calculateNextAttempt(currentRetryCount)
                );
            }

            outboxEventRepository.save(event);
        }
    }

    private Instant calculateNextAttempt(int retryCount) {

        long delaySeconds = Math.min(
                60,
                (long) Math.pow(2, retryCount)
        );

        return Instant.now().plusSeconds(delaySeconds);
    }
}