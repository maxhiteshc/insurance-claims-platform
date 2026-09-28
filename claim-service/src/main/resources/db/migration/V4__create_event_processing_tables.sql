CREATE TABLE processed_events (
    event_id VARCHAR2(36 CHAR) NOT NULL,
    event_type VARCHAR2(100 CHAR) NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_claim_processed_events PRIMARY KEY (event_id)
);

CREATE TABLE claim_event_audit (
    id RAW(16) NOT NULL,
    claim_id RAW(16) NOT NULL,
    event_id VARCHAR2(36 CHAR) NOT NULL,
    event_type VARCHAR2(100 CHAR) NOT NULL,
    event_version NUMBER(10) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    outcome VARCHAR2(50 CHAR) NOT NULL,
    reason VARCHAR2(500 CHAR),
    CONSTRAINT pk_claim_event_audit PRIMARY KEY (id),
    CONSTRAINT uk_claim_event_audit_event_id UNIQUE (event_id)
);

CREATE INDEX idx_claim_audit_claim_id ON claim_event_audit(claim_id);
CREATE INDEX idx_claim_audit_event_id ON claim_event_audit(event_id);
