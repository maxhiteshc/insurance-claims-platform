CREATE TABLE policies (
    id              RAW(16) NOT NULL,
    customer_id     RAW(16) NOT NULL,
    coverage_limit  NUMBER(19, 2) NOT NULL,
    valid_from      DATE NOT NULL,
    valid_to        DATE NOT NULL,
    status          VARCHAR2(20 CHAR) NOT NULL,
    version         NUMBER(19) DEFAULT 0 NOT NULL,

    CONSTRAINT pk_policies PRIMARY KEY (id),
    CONSTRAINT chk_policy_status CHECK (
        status IN ('ACTIVE', 'EXPIRED', 'CANCELLED')
    ),
    CONSTRAINT chk_policy_dates CHECK (
        valid_to >= valid_from
    )
);

CREATE INDEX idx_policy_customer_id ON policies (customer_id);
CREATE INDEX idx_policy_status ON policies (status);
