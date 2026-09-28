package com.insurance.claim.client;

import com.insurance.claim.saga.SettlementSaga;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PolicyServiceClient {

    private final RestClient restClient;

    public PolicyServiceClient(RestClient.Builder builder,
                               org.springframework.core.env.Environment environment) {
        this.restClient = builder
                .baseUrl(environment.getProperty("clients.policy-service.base-url", "http://localhost:8082"))
                .build();
    }

    public ReservationResponse reserve(UUID claimId, UUID policyId, UUID customerId, BigDecimal amount) {
        try {
            return restClient.post()
                    .uri("/api/v1/policies/{policyId}/reservations", policyId)
                    .body(new ReservationRequest(claimId, customerId, amount))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new PolicyClientException("Policy reservation failed: " + response.getStatusCode());
                    })
                    .body(ReservationResponse.class);
        } catch (RestClientException exception) {
            throw new PolicyClientException("Unable to reserve coverage", exception);
        }
    }

    public void release(UUID claimId, String reservationId) {
        try {
            restClient.post()
                    .uri("/api/v1/policies/reservations/{reservationId}/release", reservationId)
                    .body(new ReleaseRequest(claimId))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new PolicyClientException("Policy reservation release failed: " + response.getStatusCode());
                    })
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            throw new PolicyClientException("Unable to release reserved coverage", exception);
        }
    }

    public record ReservationRequest(UUID claimId, UUID customerId, BigDecimal amount) {}
    public record ReleaseRequest(UUID claimId) {}
    public record ReservationResponse(String reservationId, UUID claimId, UUID policyId,
                                      BigDecimal amount, String status) {}

    public static class PolicyClientException extends RuntimeException {
        public PolicyClientException(String message) { super(message); }
        public PolicyClientException(String message, Throwable cause) { super(message, cause); }
    }
}
