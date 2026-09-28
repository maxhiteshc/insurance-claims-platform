package com.insurance.claim.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClaimEventAuditRepository extends JpaRepository<ClaimEventAudit, UUID> {
}
