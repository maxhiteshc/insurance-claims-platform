package com.insurance.policy.service;

import com.insurance.policy.dto.ReservationResponse;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="policy_reservations", indexes=@Index(name="idx_reservation_claim_id", columnList="claim_id", unique=true))
public class PolicyReservation {
    @Id @Column(name="id", length=36) private String id;
    @Column(name="claim_id", nullable=false, unique=true) private UUID claimId;
    @Column(name="policy_id", nullable=false) private UUID policyId;
    @Column(name="amount", nullable=false, precision=19, scale=2) private BigDecimal amount;
    @Column(name="status", nullable=false, length=20) private String status;
    protected PolicyReservation() {}
    public PolicyReservation(String id, UUID claimId, UUID policyId, BigDecimal amount) { this.id=id; this.claimId=claimId; this.policyId=policyId; this.amount=amount; this.status="RESERVED"; }
    public UUID getClaimId(){return claimId;} public UUID getPolicyId(){return policyId;} public BigDecimal getAmount(){return amount;} public String getStatus(){return status;}
    public void release(){status="RELEASED";}
    public ReservationResponse toResponse(){return new ReservationResponse(id,claimId,policyId,amount,status);}
}
