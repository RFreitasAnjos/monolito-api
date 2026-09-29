ALTER TABLE customers DROP COLUMN cpf;
ALTER TABLE customers DROP COLUMN password_hash;

CREATE TABLE customer_access_codes (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    code_hash VARCHAR(100) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    consumed_at TIMESTAMP WITH TIME ZONE,
    failed_attempts INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_customer_access_codes_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE INDEX idx_customer_access_codes_customer_created
    ON customer_access_codes (customer_id, created_at);

CREATE TABLE customer_sessions (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_customer_sessions_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE INDEX idx_customer_sessions_customer ON customer_sessions (customer_id);
CREATE INDEX idx_customer_sessions_expires_at ON customer_sessions (expires_at);
