package com.insurance.payment.controller;

import com.insurance.payment.dto.ErrorResponse;
import com.insurance.payment.service.PaymentService.PaymentNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ErrorResponse handleNotFound(PaymentNotFoundException exception, HttpServletRequest request) {
        return new ErrorResponse(Instant.now(), HttpStatus.NOT_FOUND.value(), "Not Found", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ErrorResponse(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Bad Request", message, request.getRequestURI());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ErrorResponse handleIllegalState(IllegalStateException exception, HttpServletRequest request) {
        return new ErrorResponse(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", exception.getMessage(), request.getRequestURI());
    }
}
