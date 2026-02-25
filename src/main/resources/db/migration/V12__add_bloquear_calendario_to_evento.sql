ALTER TABLE evento ADD COLUMN bloquear_calendario BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE evento SET bloquear_calendario = TRUE WHERE status_evento = 'AGENDADO';

UPDATE evento SET status_evento = 'AGENDADO' WHERE status_evento = 'PENDENTE';
