package com.insurance.claim.saga;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SettlementSagaRepository extends JpaRepository<SettlementSaga, UUID> {
    Optional<SettlementSaga> findByClaimId(UUID claimId);
    List<SettlementSaga> findTop20ByStatusInAndNextAttemptAtLessThanEqualOrderByNextAttemptAtAsc(
            List<SettlementSagaStatus> statuses, Instant now, Pageable pageable);
}
