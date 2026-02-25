CREATE TABLE password_reset_codes (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    codigo          VARCHAR(4)      NOT NULL,
    expiracao       TIMESTAMP       NOT NULL,
    utilizado       BOOLEAN         NOT NULL DEFAULT FALSE,
    data_criacao    TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_password_reset_codes_user_id ON password_reset_codes(user_id);
CREATE INDEX idx_password_reset_codes_codigo ON password_reset_codes(codigo);
