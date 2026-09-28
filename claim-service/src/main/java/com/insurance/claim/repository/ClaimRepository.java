package com.insurance.claim.repository;

import com.insurance.claim.domain.Claim;
import com.insurance.claim.domain.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {

    Page<Claim> findByCustomerId(
            UUID customerId,
            Pageable pageable
    );

    Page<Claim> findByStatus(
            ClaimStatus status,
            Pageable pageable
    );
}