package com.insurance.payment.controller;

import com.insurance.payment.dto.PaymentRequest;
import com.insurance.payment.dto.PaymentResponse;
import com.insurance.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADJUSTER','ADMIN')")
    public ResponseEntity<PaymentResponse> execute(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/claim/{claimId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADJUSTER','ADMIN')")
    public PaymentResponse getByClaimId(@PathVariable UUID claimId) {
        return paymentService.getByClaimId(claimId);
    }
}
