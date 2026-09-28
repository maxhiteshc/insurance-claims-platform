package com.insurance.claim.client;

import com.insurance.claim.saga.SettlementSaga;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PaymentServiceClient {

    private final RestClient restClient;

    public PaymentServiceClient(RestClient.Builder builder, Environment environment) {
        this.restClient = builder
                .baseUrl(environment.getProperty("clients.payment-service.base-url", "http://localhost:8083"))
                .build();
    }

    public PaymentResponse execute(UUID claimId, UUID customerId, BigDecimal amount) {
        try {
            return restClient.post()
                    .uri("/api/v1/payments")
                    .body(new PaymentRequest(claimId, customerId, amount))
                    .retrieve()
                    .body(PaymentResponse.class);
        } catch (RestClientException exception) {
            throw new PaymentClientException("Unable to execute payment", exception);
        }
    }

    public record PaymentRequest(UUID claimId, UUID customerId, BigDecimal amount) {}
    public record PaymentResponse(UUID id, UUID claimId, UUID customerId, BigDecimal amount,
                                  String status, String transactionReference, String failureReason) {}

    public static class PaymentClientException extends RuntimeException {
        public PaymentClientException(String message, Throwable cause) { super(message, cause); }
    }
}
