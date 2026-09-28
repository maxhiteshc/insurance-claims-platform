CREATE TABLE claims (
                        id              RAW(16) NOT NULL,
                        policy_id       RAW(16) NOT NULL,
                        customer_id     RAW(16) NOT NULL,
                        claim_amount    NUMBER(19, 2) NOT NULL,
                        status          VARCHAR2(50 CHAR) NOT NULL,
                        submitted_at   TIMESTAMP WITH TIME ZONE NOT NULL,
                        updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,
                        version         NUMBER(19) DEFAULT 0 NOT NULL,

                        CONSTRAINT pk_claims PRIMARY KEY (id),
                        CONSTRAINT chk_claim_status CHECK (
                            status IN (
                                       'SUBMITTED',
                                       'UNDER_REVIEW',
                                       'APPROVED',
                                       'REJECTED',
                                       'SETTLEMENT_PENDING',
                                       'SETTLED'
                                )
                            )
);

CREATE INDEX idx_claim_policy_id
    ON claims (policy_id);

CREATE INDEX idx_claim_customer_id
    ON claims (customer_id);

CREATE INDEX idx_claim_status
    ON claims (status);