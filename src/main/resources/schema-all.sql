DROP TABLE clientes IF EXISTS;

CREATE TABLE clientes  (
    id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    nome VARCHAR(20),
    sobrenome VARCHAR(20),
    regiao VARCHAR(20)
);
