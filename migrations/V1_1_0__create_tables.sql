CREATE TYPE role_type AS ENUM ('ADMIN', 'USER');
CREATE TYPE status_type AS ENUM ('ACTIVE', 'EXPIRED', 'USED');

CREATE TABLE config (
                            id SERIAL PRIMARY KEY,
                            code_length INTEGER NOT NULL,
                            ttl_seconds INTEGER NOT NULL
);

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role role_type NOT NULL
);

CREATE TABLE codes (
                           id SERIAL PRIMARY KEY,
                           user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                           code VARCHAR(10) NOT NULL,
                           status status_type NOT NULL,
                           created_at TIMESTAMP NOT NULL,
                           operation_id VARCHAR(255)
);