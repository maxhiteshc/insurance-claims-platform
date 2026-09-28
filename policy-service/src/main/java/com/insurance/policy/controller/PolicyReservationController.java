package com.insurance.policy.controller;
import com.insurance.policy.dto.ReservationResponse;
import com.insurance.policy.dto.ReserveCoverageRequest;
import com.insurance.policy.service.PolicyReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.UUID;
@RestController @RequestMapping("/api/v1/policies")
public class PolicyReservationController{
 private final PolicyReservationService service;
 public PolicyReservationController(PolicyReservationService service){this.service=service;}
 @PostMapping("/{policyId}/reservations") public ResponseEntity<ReservationResponse> reserve(@PathVariable UUID policyId,@Valid @RequestBody ReserveCoverageRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(service.reserve(policyId,request));}
 @PostMapping("/reservations/{reservationId}/release") public ResponseEntity<Void> release(@PathVariable String reservationId,@RequestBody ReleaseRequest request){service.release(reservationId,request.claimId());return ResponseEntity.noContent().build();}
 public record ReleaseRequest(UUID claimId){}
}
