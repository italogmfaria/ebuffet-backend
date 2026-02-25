
CREATE TABLE reserva_comida (
    reserva_id BIGINT NOT NULL,
    comida_id BIGINT NOT NULL,
    PRIMARY KEY (reserva_id, comida_id),
    CONSTRAINT fk_reserva_comida_reserva FOREIGN KEY (reserva_id) REFERENCES reserva(id),
    CONSTRAINT fk_reserva_comida_comida FOREIGN KEY (comida_id) REFERENCES comida(id)
);

INSERT INTO reserva_comida (reserva_id, comida_id)
SELECT r.id, cc.comida_id
FROM reserva r
INNER JOIN cardapio_comida cc ON r.cardapio_id = cc.cardapio_id
WHERE r.cardapio_id IS NOT NULL;

ALTER TABLE reserva DROP CONSTRAINT IF EXISTS fk_reserva_cardapio;
ALTER TABLE reserva DROP COLUMN cardapio_id;

DROP TABLE IF EXISTS cardapio_comida;
DROP TABLE IF EXISTS cardapio;
