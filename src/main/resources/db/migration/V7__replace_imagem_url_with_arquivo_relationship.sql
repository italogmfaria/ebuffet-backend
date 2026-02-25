ALTER TABLE comida DROP COLUMN IF EXISTS imagem_url;
ALTER TABLE servico DROP COLUMN IF EXISTS imagem_url;

ALTER TABLE comida ADD COLUMN imagem_id BIGINT;
ALTER TABLE servico ADD COLUMN imagem_id BIGINT;

ALTER TABLE comida ADD CONSTRAINT fk_comida_imagem FOREIGN KEY (imagem_id) REFERENCES arquivo(id);
ALTER TABLE servico ADD CONSTRAINT fk_servico_imagem FOREIGN KEY (imagem_id) REFERENCES arquivo(id);
