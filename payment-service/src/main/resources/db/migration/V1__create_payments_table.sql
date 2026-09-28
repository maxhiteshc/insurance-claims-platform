CREATE TABLE payments (
    id RAW(16) NOT NULL,
    claim_id RAW(16) NOT NULL,
    customer_id RAW(16) NOT NULL,
    amount NUMBER(19, 2) NOT NULL,
    status VARCHAR2(20 CHAR) NOT NULL,
    transaction_reference VARCHAR2(100 CHAR),
    failure_reason VARCHAR2(500 CHAR),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_payments PRIMARY KEY (id),
    CONSTRAINT uk_payments_claim_id UNIQUE (claim_id),
    CONSTRAINT chk_payment_status CHECK (status IN ('PROCESSING','SUCCESS','FAILED')),
    CONSTRAINT chk_payment_amount CHECK (amount > 0)
);

CREATE INDEX idx_payment_claim_id ON payments(claim_id);
CREATE INDEX idx_payment_customer_id ON payments(customer_id);
CREATE INDEX idx_payment_status ON payments(status);
