CREATE TABLE fingerprint_events (
    event_id SERIAL PRIMARY KEY,
    fingerprint BIGINT NOT NULL UNIQUE,
    last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);