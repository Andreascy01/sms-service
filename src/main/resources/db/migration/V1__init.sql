CREATE TABLE messages (
    id UUID PRIMARY KEY,
    source_number VARCHAR(20) NOT NULL,
    destination_number VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    error_code VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_messages_status ON messages (status);

CREATE INDEX idx_messages_created_at ON messages (created_at);