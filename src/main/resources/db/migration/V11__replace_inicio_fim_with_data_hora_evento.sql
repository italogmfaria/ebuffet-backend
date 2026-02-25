
ALTER TABLE evento ADD COLUMN data_evento DATE;
ALTER TABLE evento ADD COLUMN hora_evento TIME;

UPDATE evento SET
    data_evento = inicio::date,
    hora_evento = inicio::time
WHERE inicio IS NOT NULL;

ALTER TABLE evento DROP COLUMN IF EXISTS inicio;
ALTER TABLE evento DROP COLUMN IF EXISTS fim;
