package com.insurance.policy.controller;

import com.insurance.policy.service.PolicyReservationService.PolicyReservationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class PolicyReservationExceptionHandler {
    @ExceptionHandler(PolicyReservationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handle(PolicyReservationException exception) {
        return Map.of("error", "POLICY_RESERVATION_FAILED", "message", exception.getMessage());
    }
}
