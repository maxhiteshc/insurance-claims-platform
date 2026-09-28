package com.insurance.policy.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "policies", indexes = {
        @Index(name = "idx_policy_customer_id", columnList = "customer_id"),
        @Index(name = "idx_policy_status", columnList = "status")
})
public class Policy {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "customer_id", nullable = false) private UUID customerId;
    @Column(name = "coverage_limit", nullable = false, precision = 19, scale = 2) private BigDecimal coverageLimit;
    @Column(name = "reserved_amount", nullable = false, precision = 19, scale = 2) private BigDecimal reservedAmount;
    @Column(name = "valid_from", nullable = false) private LocalDate validFrom;
    @Column(name = "valid_to", nullable = false) private LocalDate validTo;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private PolicyStatus status;
    @Version private Long version;
    protected Policy() {}
    public UUID getId(){return id;} public UUID getCustomerId(){return customerId;} public BigDecimal getCoverageLimit(){return coverageLimit;}
    public BigDecimal getReservedAmount(){return reservedAmount;} public LocalDate getValidFrom(){return validFrom;} public LocalDate getValidTo(){return validTo;}
    public PolicyStatus getStatus(){return status;} public Long getVersion(){return version;}
    public boolean isActiveOn(LocalDate date){return status==PolicyStatus.ACTIVE&&!date.isBefore(validFrom)&&!date.isAfter(validTo);}
    public BigDecimal availableCoverage(){return coverageLimit.subtract(reservedAmount);}
    public void reserve(BigDecimal amount){reservedAmount=reservedAmount.add(amount);}
    public void release(BigDecimal amount){reservedAmount=reservedAmount.subtract(amount);if(reservedAmount.signum()<0)reservedAmount=BigDecimal.ZERO;}
}
