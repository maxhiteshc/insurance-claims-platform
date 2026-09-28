CREATE TABLE outbox_events (
                               id                RAW(16) NOT NULL,
                               aggregate_type    VARCHAR2(100 CHAR) NOT NULL,
                               aggregate_id      RAW(16) NOT NULL,
                               event_type        VARCHAR2(100 CHAR) NOT NULL,
                               payload           CLOB NOT NULL,
                               status            VARCHAR2(20 CHAR) NOT NULL,
                               created_at        TIMESTAMP WITH TIME ZONE NOT NULL,
                               published_at      TIMESTAMP WITH TIME ZONE,
                               retry_count       NUMBER(10) DEFAULT 0 NOT NULL,
                               next_attempt_at   TIMESTAMP WITH TIME ZONE,

                               CONSTRAINT pk_outbox_events PRIMARY KEY (id),

                               CONSTRAINT chk_outbox_status CHECK (
                                   status IN (
                                              'PENDING',
                                              'PUBLISHED',
                                              'FAILED'
                                       )
                                   )
);

CREATE INDEX idx_outbox_status_created
    ON outbox_events (status, created_at);

CREATE INDEX idx_outbox_aggregate_id
    ON outbox_events (aggregate_id);