CREATE TABLE employees (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    position VARCHAR(30) NOT NULL,
    access_role VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(160) NOT NULL,
    updated_by VARCHAR(160) NOT NULL,
    deactivated_at TIMESTAMP WITH TIME ZONE,
    deactivated_by VARCHAR(160),
    version BIGINT NOT NULL DEFAULT 0
);
