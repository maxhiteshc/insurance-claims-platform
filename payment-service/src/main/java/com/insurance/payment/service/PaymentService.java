package com.insurance.payment.service;

import com.insurance.payment.config.PaymentProperties;
import com.insurance.payment.domain.Payment;
import com.insurance.payment.domain.PaymentStatus;
import com.insurance.payment.dto.PaymentRequest;
import com.insurance.payment.dto.PaymentResponse;
import com.insurance.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProperties paymentProperties;

    public PaymentService(PaymentRepository paymentRepository, PaymentProperties paymentProperties) {
        this.paymentRepository = paymentRepository;
        this.paymentProperties = paymentProperties;
    }

    @Transactional
    public PaymentResponse execute(PaymentRequest request) {
        validateFailureRate();

        Payment existing = paymentRepository.findByClaimId(request.claimId()).orElse(null);
        if (existing != null) {
            return PaymentResponse.from(existing);
        }

        Payment payment = new Payment(request.claimId(), request.customerId(), request.amount());
        payment = paymentRepository.save(payment);

        if (ThreadLocalRandom.current().nextDouble() < paymentProperties.getFailureRate()) {
            payment.markFailed("SIMULATED_PAYMENT_FAILURE");
        } else {
            payment.markSuccess("PAY-" + UUID.randomUUID());
        }

        return PaymentResponse.from(paymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getByClaimId(UUID claimId) {
        return paymentRepository.findByClaimId(claimId)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new PaymentNotFoundException(claimId));
    }

    private void validateFailureRate() {
        double rate = paymentProperties.getFailureRate();
        if (rate < 0 || rate > 1) {
            throw new IllegalStateException("payment.failure-rate must be between 0.0 and 1.0");
        }
    }

    public static class PaymentNotFoundException extends RuntimeException {
        public PaymentNotFoundException(UUID claimId) {
            super("Payment not found for claim " + claimId);
        }
    }
}
