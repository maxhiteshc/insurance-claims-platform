ALTER TABLE claims DROP CONSTRAINT chk_claim_status;
ALTER TABLE claims ADD CONSTRAINT chk_claim_status CHECK (
    status IN ('SUBMITTED','UNDER_REVIEW','APPROVED','REJECTED','SETTLEMENT_PENDING','SETTLED','SETTLEMENT_FAILED')
);

CREATE TABLE settlement_sagas (
    id RAW(16) NOT NULL,
    claim_id RAW(16) NOT NULL,
    policy_id RAW(16) NOT NULL,
    customer_id RAW(16) NOT NULL,
    amount NUMBER(19,2) NOT NULL,
    status VARCHAR2(40 CHAR) NOT NULL,
    reservation_id VARCHAR2(100 CHAR),
    payment_id VARCHAR2(100 CHAR),
    retry_count NUMBER(10) DEFAULT 0 NOT NULL,
    last_error VARCHAR2(1000 CHAR),
    next_attempt_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_settlement_sagas PRIMARY KEY (id),
    CONSTRAINT uk_settlement_saga_claim UNIQUE (claim_id),
    CONSTRAINT chk_settlement_saga_status CHECK (
        status IN ('RESERVATION_PENDING','PAYMENT_PENDING','COMPENSATION_PENDING','COMPLETED','COMPENSATED','FAILED')
    )
);
CREATE INDEX idx_saga_status_next_attempt ON settlement_sagas(status, next_attempt_at);
