CREATE TABLE notificacoes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    titulo VARCHAR(255) NOT NULL,
    mensagem VARCHAR(500) NOT NULL,
    lida BOOLEAN NOT NULL DEFAULT false,
    reserva_id BIGINT REFERENCES reserva(id) ON DELETE SET NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_leitura TIMESTAMP,
    CONSTRAINT chk_notificacoes_status CHECK (status IN ('ATIVO', 'INATIVO'))
);

CREATE INDEX idx_notificacoes_usuario_id ON notificacoes(usuario_id);
CREATE INDEX idx_notificacoes_lida ON notificacoes(lida);
CREATE INDEX idx_notificacoes_data_criacao ON notificacoes(data_criacao DESC);
