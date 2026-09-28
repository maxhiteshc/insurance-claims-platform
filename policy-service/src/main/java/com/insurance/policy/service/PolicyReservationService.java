package com.insurance.policy.service;

import com.insurance.policy.domain.Policy;
import com.insurance.policy.dto.ReservationResponse;
import com.insurance.policy.dto.ReserveCoverageRequest;
import com.insurance.policy.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class PolicyReservationService {
    private final PolicyRepository policyRepository;
    private final PolicyReservationRepository reservationRepository;
    public PolicyReservationService(PolicyRepository policyRepository, PolicyReservationRepository reservationRepository) {
        this.policyRepository = policyRepository;
        this.reservationRepository = reservationRepository;
    }
    @Transactional
    public ReservationResponse reserve(UUID policyId, ReserveCoverageRequest request) {
        PolicyReservation existing = reservationRepository.findByClaimId(request.claimId()).orElse(null);
        if (existing != null) return existing.toResponse();
        Policy policy = policyRepository.findById(policyId).orElseThrow(() -> new PolicyReservationException("Policy not found: " + policyId));
        if (!policy.getCustomerId().equals(request.customerId())) throw new PolicyReservationException("Customer does not own policy");
        if (!policy.isActiveOn(LocalDate.now())) throw new PolicyReservationException("Policy is not active");
        if (policy.availableCoverage().compareTo(request.amount()) < 0) throw new PolicyReservationException("Insufficient available coverage");
        policy.reserve(request.amount());
        policyRepository.save(policy);
        return reservationRepository.save(new PolicyReservation(java.util.UUID.randomUUID().toString(), request.claimId(), policyId, request.amount())).toResponse();
    }
    @Transactional
    public void release(String reservationId, UUID claimId) {
        PolicyReservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new PolicyReservationException("Reservation not found: " + reservationId));
        if (!reservation.getClaimId().equals(claimId)) throw new PolicyReservationException("Reservation does not belong to claim");
        if ("RELEASED".equals(reservation.getStatus())) return;
        Policy policy = policyRepository.findById(reservation.getPolicyId()).orElseThrow(() -> new PolicyReservationException("Policy not found: " + reservation.getPolicyId()));
        policy.release(reservation.getAmount());
        policyRepository.save(policy);
        reservation.release();
        reservationRepository.save(reservation);
    }
    public static class PolicyReservationException extends RuntimeException { public PolicyReservationException(String message) { super(message); } }
}
