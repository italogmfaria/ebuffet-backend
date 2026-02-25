ALTER TABLE users ADD COLUMN foto_id BIGINT;

ALTER TABLE users ADD CONSTRAINT fk_user_foto
    FOREIGN KEY (foto_id) REFERENCES arquivo(id);
