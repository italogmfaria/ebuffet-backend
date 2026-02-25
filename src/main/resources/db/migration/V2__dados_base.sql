CREATE EXTENSION IF NOT EXISTS pgcrypto;



INSERT INTO endereco (rua, numero, bairro, cidade, estado, cep, complemento, data_criacao, data_atualizacao, status)
VALUES ('Rua das Palmeiras', '320', 'Jardim Europa', 'São Paulo', 'SP', '01431-000', 'Salão Principal',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO');

INSERT INTO endereco (rua, numero, bairro, cidade, estado, cep, complemento, data_criacao, data_atualizacao, status)
VALUES ('Avenida Paulista', '1578', 'Bela Vista', 'São Paulo', 'SP', '01310-200', 'Conjunto 501',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO');

INSERT INTO endereco (rua, numero, bairro, cidade, estado, cep, complemento, data_criacao, data_atualizacao, status)
VALUES ('Rua das Flores', '123', 'Jardim Botânico', 'Rio de Janeiro', 'RJ', '22460-030', 'Casa',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO');

INSERT INTO endereco (rua, numero, bairro, cidade, estado, cep, complemento, data_criacao, data_atualizacao, status)
VALUES ('Rua Augusta', '456', 'Consolação', 'São Paulo', 'SP', '01305-000', 'Apto 802',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO');


INSERT INTO users (nome, email, telefone, senha, data_criacao, data_atualizacao, status, role, buffet_id)
VALUES (
           'Admin Sistema',
           'admin@ebuffet.com',
           '11999999999',
           crypt('admin123', gen_salt('bf', 10)),
           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
           'ATIVO',
           'ADMIN',
           NULL
       );

INSERT INTO users (nome, email, telefone, senha, data_criacao, data_atualizacao, status, role, buffet_id)
VALUES (
           'Carlos Oliveira',
           'carlos@buffetoliveira.com',
           '11988887777',
           crypt('buffet123', gen_salt('bf', 10)),
           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
           'ATIVO',
           'BUFFET',
           NULL
       );

INSERT INTO users (nome, email, telefone, senha, data_criacao, data_atualizacao, status, role, buffet_id)
VALUES (
           'Ricardo Santos',
           'ricardo@ebuffet.com',
           '11977776666',
           crypt('buffet123', gen_salt('bf', 10)),
           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
           'ATIVO',
           'BUFFET',
           NULL
       );


INSERT INTO buffet (nome, endereco_id, telefone, email, owner_id, data_criacao, data_atualizacao, status)
VALUES ('Buffet Oliveira',
        (SELECT id FROM endereco WHERE rua = 'Rua das Palmeiras'),
        '11987654321',
        'contato@buffetoliveira.com',
        (SELECT id FROM users WHERE email = 'carlos@buffetoliveira.com'),
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO');

INSERT INTO buffet (nome, endereco_id, telefone, email, owner_id, data_criacao, data_atualizacao, status)
VALUES ('eBuffet',
        (SELECT id FROM endereco WHERE rua = 'Avenida Paulista'),
        '11912345678',
        'contato@ebuffet.com',
        (SELECT id FROM users WHERE email = 'ricardo@ebuffet.com'),
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO');

UPDATE users
SET buffet_id = (SELECT id FROM buffet WHERE nome = 'Buffet Oliveira')
WHERE email = 'carlos@buffetoliveira.com';

UPDATE users
SET buffet_id = (SELECT id FROM buffet WHERE nome = 'eBuffet')
WHERE email = 'ricardo@ebuffet.com';


INSERT INTO users (nome, email, telefone, senha, data_criacao, data_atualizacao, status, role, buffet_id)
VALUES (
           'Maria Santos',
           'maria.santos@email.com',
           '11966665555',
           crypt('cliente123', gen_salt('bf', 10)),
           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
           'ATIVO',
           'CLIENTE',
           (SELECT id FROM buffet WHERE nome = 'Buffet Oliveira')
       );

INSERT INTO users (nome, email, telefone, senha, data_criacao, data_atualizacao, status, role, buffet_id)
VALUES (
           'Pedro Oliveira',
           'pedro.oliveira@email.com',
           '11955554444',
           crypt('cliente123', gen_salt('bf', 10)),
           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
           'ATIVO',
           'CLIENTE',
           (SELECT id FROM buffet WHERE nome = 'eBuffet')
       );


INSERT INTO servico (nome, descricao, categoria, buffet_id, data_criacao, data_atualizacao, status)
VALUES
    ('Decoração Premium', 'Decoração completa com flores naturais, iluminação especial e arranjos personalizados',
     'CASAMENTO', (SELECT id FROM buffet WHERE nome = 'Buffet Oliveira'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO'),

    ('DJ Profissional', 'DJ com equipamento de som e iluminação de alta qualidade para animar sua festa',
     'ANIVERSARIO', (SELECT id FROM buffet WHERE nome = 'Buffet Oliveira'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO'),

    ('Fotografia e Filmagem', 'Registro profissional de todos os momentos especiais do seu evento',
     'FORMATURA', (SELECT id FROM buffet WHERE nome = 'Buffet Oliveira'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO');

INSERT INTO servico (nome, descricao, categoria, buffet_id, data_criacao, data_atualizacao, status)
VALUES
    ('Buffet Infantil', 'Monitores, brinquedos e atividades recreativas para as crianças',
     'ANIVERSARIO', (SELECT id FROM buffet WHERE nome = 'eBuffet'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO'),

    ('Serviço de Garçons', 'Equipe profissional de garçons para atendimento impecável',
     'CONFRATERNIZACAO', (SELECT id FROM buffet WHERE nome = 'eBuffet'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO');


INSERT INTO comida (nome, descricao, categoria, buffet_id, data_criacao, data_atualizacao, status)
VALUES
    ('Filé Mignon ao Molho Madeira', 'Filé mignon grelhado ao ponto com delicioso molho madeira',
     'CASAMENTO', (SELECT id FROM buffet WHERE nome = 'Buffet Oliveira'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO'),

    ('Salmão Grelhado', 'Salmão fresco grelhado com ervas finas e limão siciliano',
     'CASAMENTO', (SELECT id FROM buffet WHERE nome = 'Buffet Oliveira'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO'),

    ('Risoto de Funghi', 'Risoto cremoso com mix de cogumelos nobres',
     'JANTAR', (SELECT id FROM buffet WHERE nome = 'Buffet Oliveira'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO'),

    ('Caesar Salad', 'Salada caesar com alface romana, croutons e parmesão',
     'JANTAR', (SELECT id FROM buffet WHERE nome = 'Buffet Oliveira'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO'),

    ('Petit Gateau', 'Bolinho de chocolate com recheio cremoso e sorvete',
     'CASAMENTO', (SELECT id FROM buffet WHERE nome = 'Buffet Oliveira'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO');

INSERT INTO comida (nome, descricao, categoria, buffet_id, data_criacao, data_atualizacao, status)
VALUES
    ('Lasanha à Bolonhesa', 'Lasanha tradicional com molho bolonhesa e queijos especiais',
     'ALMOCO', (SELECT id FROM buffet WHERE nome = 'eBuffet'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO'),

    ('Camarão ao Alho e Óleo', 'Camarões grandes salteados com alho, azeite e pimenta',
     'JANTAR', (SELECT id FROM buffet WHERE nome = 'eBuffet'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO'),

    ('Costela ao Bafo', 'Costela bovina assada lentamente até desmanchar',
     'CONFRATERNIZACAO', (SELECT id FROM buffet WHERE nome = 'eBuffet'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO'),

    ('Arroz à Grega', 'Arroz branco com legumes, uvas passas e castanhas',
     'ALMOCO', (SELECT id FROM buffet WHERE nome = 'eBuffet'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO'),

    ('Batata Rosti', 'Batatas raladas e douradas crocantes',
     'ANIVERSARIO', (SELECT id FROM buffet WHERE nome = 'eBuffet'),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO');


INSERT INTO cardapio (data_criacao, data_atualizacao, status)
VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO');

INSERT INTO cardapio (data_criacao, data_atualizacao, status)
VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ATIVO');

INSERT INTO cardapio_comida (cardapio_id, comida_id)
SELECT
    (SELECT id FROM cardapio ORDER BY id LIMIT 1),
    id
FROM comida
WHERE nome IN ('Filé Mignon ao Molho Madeira', 'Salmão Grelhado', 'Risoto de Funghi', 'Caesar Salad', 'Petit Gateau');

INSERT INTO cardapio_comida (cardapio_id, comida_id)
SELECT
    (SELECT id FROM cardapio ORDER BY id LIMIT 1 OFFSET 1),
    id
FROM comida
WHERE nome IN ('Lasanha à Bolonhesa', 'Camarão ao Alho e Óleo', 'Arroz à Grega', 'Batata Rosti', 'Costela ao Bafo');


INSERT INTO reserva (cliente_id, buffet_id, status_reserva, endereco_id, qtd_pessoas, horario_desejado,
                     data_desejada, cardapio_id, observacoes, data_criacao, data_atualizacao, status)
VALUES (
           (SELECT id FROM users WHERE email = 'maria.santos@email.com'),
           (SELECT id FROM buffet WHERE nome = 'Buffet Oliveira'),
           'PENDENTE',
           (SELECT id FROM endereco WHERE rua = 'Rua das Flores'),
           150,
           '18:00:00',
           CURRENT_DATE + INTERVAL '30 days',
           (SELECT id FROM cardapio ORDER BY id LIMIT 1),
    'Casamento ao ar livre, precisamos de tendas em caso de chuva',
            CURRENT_TIMESTAMP,
            CURRENT_TIMESTAMP,
    'ATIVO'
    );

INSERT INTO reserva (cliente_id, buffet_id, status_reserva, endereco_id, qtd_pessoas, horario_desejado,
                     data_desejada, cardapio_id, observacoes, data_criacao, data_atualizacao, status)
VALUES (
           (SELECT id FROM users WHERE email = 'pedro.oliveira@email.com'),
           (SELECT id FROM buffet WHERE nome = 'eBuffet'),
           'PENDENTE',
           (SELECT id FROM endereco WHERE rua = 'Rua Augusta'),
           80,
           '15:00:00',
           CURRENT_DATE + INTERVAL '45 days',
           (SELECT id FROM cardapio ORDER BY id LIMIT 1 OFFSET 1),
    'Festa de 50 anos, preferência por música ao vivo',
            CURRENT_TIMESTAMP,
            CURRENT_TIMESTAMP,
    'ATIVO'
    );

INSERT INTO reserva_servico (reserva_id, servico_id)
SELECT
    (SELECT id FROM reserva WHERE cliente_id = (SELECT id FROM users WHERE email = 'maria.santos@email.com')),
    id
FROM servico
WHERE nome IN ('Decoração Premium', 'Fotografia e Filmagem');

INSERT INTO reserva_servico (reserva_id, servico_id)
SELECT
    (SELECT id FROM reserva WHERE cliente_id = (SELECT id FROM users WHERE email = 'pedro.oliveira@email.com')),
    id
FROM servico
WHERE nome IN ('Buffet Infantil', 'Serviço de Garçons');
