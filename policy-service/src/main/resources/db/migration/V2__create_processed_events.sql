CREATE TABLE processed_events (
    event_id      VARCHAR2(36 CHAR) NOT NULL,
    event_type    VARCHAR2(100 CHAR) NOT NULL,
    processed_at  TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_processed_events PRIMARY KEY (event_id)
);
