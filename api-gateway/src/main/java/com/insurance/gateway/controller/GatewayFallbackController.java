package com.insurance.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class GatewayFallbackController {

    @GetMapping("/claim")
    public ResponseEntity<Map<String, String>> claimFallback() {
        return fallbackResponse();
    }

    @GetMapping("/policy")
    public ResponseEntity<Map<String, String>> policyFallback() {
        return fallbackResponse();
    }

    @GetMapping("/payment")
    public ResponseEntity<Map<String, String>> paymentFallback() {
        return fallbackResponse();
    }

    private ResponseEntity<Map<String, String>> fallbackResponse() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "code", "SERVICE_UNAVAILABLE",
                        "message", "The requested service is temporarily unavailable."
                ));
    }
}
