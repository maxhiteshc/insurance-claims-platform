ALTER TABLE policies ADD reserved_amount NUMBER(19,2) DEFAULT 0 NOT NULL;
CREATE TABLE policy_reservations (
 id VARCHAR2(36 CHAR) NOT NULL,
 claim_id RAW(16) NOT NULL,
 policy_id RAW(16) NOT NULL,
 amount NUMBER(19,2) NOT NULL,
 status VARCHAR2(20 CHAR) NOT NULL,
 CONSTRAINT pk_policy_reservations PRIMARY KEY (id),
 CONSTRAINT uk_policy_reservation_claim UNIQUE (claim_id),
 CONSTRAINT chk_policy_reservation_status CHECK (status IN ('RESERVED','RELEASED'))
);
CREATE INDEX idx_reservation_claim_id ON policy_reservations(claim_id);
CREATE INDEX idx_reservation_policy_id ON policy_reservations(policy_id);
