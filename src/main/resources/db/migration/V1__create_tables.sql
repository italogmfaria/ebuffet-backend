CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       nome VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       telefone VARCHAR(255),
                       senha VARCHAR(255) NOT NULL,
                       buffet_id BIGINT,
                       role VARCHAR(50) NOT NULL,
                       data_criacao TIMESTAMP NOT NULL,
                       data_atualizacao TIMESTAMP NOT NULL,
                       status VARCHAR(50) NOT NULL,
                       CONSTRAINT uk_users_email UNIQUE (email),
                       CONSTRAINT uk_users_telefone UNIQUE (telefone)
);

CREATE TABLE endereco (
                          id BIGSERIAL PRIMARY KEY,
                          rua VARCHAR(120) NOT NULL,
                          numero VARCHAR(20) NOT NULL,
                          bairro VARCHAR(80),
                          cidade VARCHAR(80) NOT NULL,
                          estado VARCHAR(2) NOT NULL,
                          cep VARCHAR(9) NOT NULL,
                          complemento VARCHAR(120),
                          data_criacao TIMESTAMP NOT NULL,
                          data_atualizacao TIMESTAMP NOT NULL,
                          status VARCHAR(50) NOT NULL
);

CREATE TABLE buffet (
                        id BIGSERIAL PRIMARY KEY,
                        nome VARCHAR(255) NOT NULL,
                        telefone VARCHAR(255),
                        email VARCHAR(255),
                        endereco_id BIGINT NOT NULL,
                        owner_id BIGINT NOT NULL,
                        data_criacao TIMESTAMP NOT NULL,
                        data_atualizacao TIMESTAMP NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        CONSTRAINT fk_buffet_endereco FOREIGN KEY (endereco_id)
                            REFERENCES endereco(id),
                        CONSTRAINT fk_buffet_owner FOREIGN KEY (owner_id)
                            REFERENCES users(id)
);

CREATE TABLE arquivo (
                         id BIGSERIAL PRIMARY KEY,
                         tipo VARCHAR(50) NOT NULL,
                         url VARCHAR(255) NOT NULL,
                         buffet_id BIGINT NOT NULL,
                         data_criacao TIMESTAMP NOT NULL,
                         data_atualizacao TIMESTAMP NOT NULL,
                         status VARCHAR(50) NOT NULL,
                         CONSTRAINT fk_arquivo_buffet FOREIGN KEY (buffet_id)
                             REFERENCES buffet(id)
);

CREATE TABLE comida (
                        id BIGSERIAL PRIMARY KEY,
                        nome VARCHAR(255) NOT NULL,
                        descricao VARCHAR(1000),
                        categoria VARCHAR(50) NOT NULL,
                        buffet_id BIGINT NOT NULL,
                        data_criacao TIMESTAMP NOT NULL,
                        data_atualizacao TIMESTAMP NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        CONSTRAINT fk_comida_buffet FOREIGN KEY (buffet_id)
                            REFERENCES buffet(id)
);

CREATE TABLE servico (
                         id BIGSERIAL PRIMARY KEY,
                         nome VARCHAR(255),
                         descricao VARCHAR(255),
                         categoria VARCHAR(50) NOT NULL,
                         buffet_id BIGINT NOT NULL,
                         data_criacao TIMESTAMP NOT NULL,
                         data_atualizacao TIMESTAMP NOT NULL,
                         status VARCHAR(50) NOT NULL,
                         CONSTRAINT fk_servico_buffet FOREIGN KEY (buffet_id)
                             REFERENCES buffet(id)
);

CREATE TABLE cardapio (
                          id BIGSERIAL PRIMARY KEY,
                          data_criacao TIMESTAMP NOT NULL,
                          data_atualizacao TIMESTAMP NOT NULL,
                          status VARCHAR(50) NOT NULL
);

CREATE TABLE cardapio_comida (
                                 cardapio_id BIGINT NOT NULL,
                                 comida_id BIGINT NOT NULL,
                                 PRIMARY KEY (cardapio_id, comida_id),
                                 CONSTRAINT fk_cardapio_comida_cardapio FOREIGN KEY (cardapio_id)
                                     REFERENCES cardapio(id),
                                 CONSTRAINT fk_cardapio_comida_comida FOREIGN KEY (comida_id)
                                     REFERENCES comida(id),
                                 CONSTRAINT uk_cardapio_comida UNIQUE (cardapio_id, comida_id)
);

CREATE TABLE reserva (
                         id BIGSERIAL PRIMARY KEY,
                         cliente_id BIGINT NOT NULL,
                         buffet_id BIGINT NOT NULL,
                         status_reserva VARCHAR(50) NOT NULL,
                         endereco_id BIGINT NOT NULL,
                         qtd_pessoas INTEGER,
                         horario_desejado TIME,
                         data_desejada DATE,
                         cardapio_id BIGINT,
                         observacoes TEXT,
                         data_criacao TIMESTAMP NOT NULL,
                         data_atualizacao TIMESTAMP NOT NULL,
                         status VARCHAR(50) NOT NULL,
                         CONSTRAINT fk_reserva_cliente FOREIGN KEY (cliente_id)
                             REFERENCES users(id),
                         CONSTRAINT fk_reserva_buffet FOREIGN KEY (buffet_id)
                             REFERENCES buffet(id),
                         CONSTRAINT fk_reserva_endereco FOREIGN KEY (endereco_id)
                             REFERENCES endereco(id),
                         CONSTRAINT fk_reserva_cardapio FOREIGN KEY (cardapio_id)
                             REFERENCES cardapio(id),
                         CONSTRAINT uk_reserva_cardapio UNIQUE (cardapio_id)
);

CREATE TABLE reserva_servico (
                                 reserva_id BIGINT NOT NULL,
                                 servico_id BIGINT NOT NULL,
                                 PRIMARY KEY (reserva_id, servico_id),
                                 CONSTRAINT fk_reserva_servico_reserva FOREIGN KEY (reserva_id)
                                     REFERENCES reserva(id),
                                 CONSTRAINT fk_reserva_servico_servico FOREIGN KEY (servico_id)
                                     REFERENCES servico(id),
                                 CONSTRAINT uk_reserva_servico UNIQUE (reserva_id, servico_id)
);

CREATE TABLE evento (
                        id BIGSERIAL PRIMARY KEY,
                        nome VARCHAR(255),
                        status_evento VARCHAR(50) NOT NULL,
                        reserva_id BIGINT NOT NULL,
                        valor DECIMAL(19,2),
                        buffet_id BIGINT NOT NULL,
                        inicio TIMESTAMP,
                        fim TIMESTAMP,
                        data_criacao TIMESTAMP NOT NULL,
                        data_atualizacao TIMESTAMP NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        CONSTRAINT fk_evento_reserva FOREIGN KEY (reserva_id)
                            REFERENCES reserva(id),
                        CONSTRAINT fk_evento_buffet FOREIGN KEY (buffet_id)
                            REFERENCES buffet(id),
                        CONSTRAINT uk_evento_reserva UNIQUE (reserva_id)
);
