package com.insurance.policy.service;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface PolicyReservationRepository extends JpaRepository<PolicyReservation,String>{ Optional<PolicyReservation> findByClaimId(UUID claimId); }
