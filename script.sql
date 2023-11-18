use projetobd

CREATE TABLE Pessoa (
    cpf varchar(14) unique,
    nome varchar(200) not null,
    email varchar(200) unique,
    senha varchar(200) not null,
    constraint pk_pessoa primary key (cpf)
);

select p.nome from pessoa p where p.email = 'teste1@teste.com'

ALTER TABLE Pessoa
ADD CONSTRAINT email_Pessoa_ck CHECK (email LIKE '%@%.%');


ALTER TABLE Pessoa
ADD CONSTRAINT cpf_Pessoa_ck CHECK (cpf LIKE '___.___.___-__');

CREATE TABLE Funcionario (
id_funcionario INT AUTO_INCREMENT PRIMARY KEY,
cargo VARCHAR(100) NOT NULL,
id_gerente INT,
cpf_pessoa VARCHAR(14) UNIQUE NOT NULL,
CONSTRAINT cpf_pessoa_fk FOREIGN KEY (cpf_pessoa) REFERENCES Pessoa(cpf) ON DELETE CASCADE,
CONSTRAINT funcionario_id_gerente_fk FOREIGN KEY (id_gerente) REFERENCES Funcionario(id_funcionario) ON DELETE CASCADE
);

create table Cliente (
id_cliente int auto_increment,
telefone varchar(50),
telefone2 varchar(50),
rua varchar(100),
numero int,
cpf_pessoa_cliente varchar(14) unique not null,
primary key (id_cliente),
constraint id_cliente_fk foreign key (cpf_pessoa_cliente) references Pessoa (cpf) on delete cascade
);

create table Transportadora (
id_transportadora int primary key,
telefone varchar (50),
nome varchar(100)
);

create table pedido (
id_pedido int auto_increment,
status varchar(50),
data_compra date,
data_chegada date,
data_prevista date,
fk_cliente_id int,
fk_id_transportadora int,

constraint pedido_pk primary key (id_pedido),
constraint cliente_fk_pedido foreign key (fk_cliente_id) references Cliente(id_cliente) on delete cascade,
constraint transportadora_fk_pedido foreign key (fk_id_transportadora) references Transportadora (id_transportadora) on delete cascade
);

INSERT INTO pedido (status, data_compra, data_prevista, fk_cliente_id, fk_id_transportadora)
VALUES (
    'Em processamento', -- ou o status desejado
    CURRENT_DATE,        -- data_compra como data atual
    DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY), -- data_prevista como data atual + 10 dias
    1, -- substitua pelo ID do cliente real
    1  -- substitua pelo ID da transportadora real
);

create table review(
id_review int primary key auto_increment,
descricao varchar(500),
nota float
);

create table Categoria(
id_categoria int primary key,
nome varchar(100)
);

create table Produto(
id_produto int auto_increment,
nome varchar(100),
preco float,
descricao varchar(500),
fk_id_categoria int,
image varchar(500),
image2 varchar(500),
image3 varchar(500),

constraint id_produto_pk primary key (id_produto),
constraint fk_id_categoria_produto foreign key (fk_id_categoria) references Categoria(id_categoria) on delete cascade
);



create table cliente_faz_review_produto (
fk_id_cliente int,
fk_id_review int,
fk_id_produto int,
constraint fk_id_cliente_faz foreign key(fk_id_cliente) references Cliente(id_cliente) on delete cascade,
constraint fk_id_review_faz foreign key(fk_id_review) references Review(id_review) on delete cascade,
constraint fk_id_produto_faz foreign key(fk_id_produto) references Produto(id_produto) on delete cascade,
primary key(fk_id_review, fk_id_produto)
);



create table Responde(
fk_id_funcionario int,
fk_id_review int,
resposta varchar(500),
constraint fk_id_funcionario_responde foreign key (fk_id_funcionario) references Funcionario(id_funcionario) on delete cascade,
constraint fk_id_review_responde foreign key (fk_id_review) references Review(id_review) on delete cascade
);

ALTER TABLE Responde
ADD CONSTRAINT pk_responde PRIMARY KEY (fk_id_review);


create table fornecedor(
id_fornecedor int primary key,
nome varchar(100) unique,
telefone varchar(50)
);

create table Possui(
fk_id_produto int,
fk_id_fornecedor int,

constraint fk_id_produto_possui foreign key(fk_id_produto) references Produto(id_produto) on delete cascade,
constraint fk_id_fornecedor_possui foreign key(fk_id_fornecedor) references Fornecedor(id_fornecedor) on delete cascade
);

create table Contem(
fk_id_produto int,
fk_id_pedido int,
quantidade int,

constraint fk_id_produto_contem foreign key(fk_id_produto) references Produto(id_produto) on delete cascade,
constraint fk_id_pedido_contem foreign key(fk_id_pedido) references Pedido(id_pedido) on delete cascade
);


-- Inserções na tabela Pessoa

INSERT INTO Pessoa (cpf, nome, email, senha) VALUES ('123.456.789-11', 'João da Silva', 'joao@example.com', '123');

INSERT INTO Pessoa (cpf, nome, email, senha) VALUES ('987.654.321-99', 'Maria Souza', 'maria@example.com', '123');

INSERT INTO Pessoa (cpf, nome, email, senha) VALUES ('111.222.333-44', 'Carlos Santos', 'carlos@example.com', '12');
INSERT INTO Pessoa (cpf, nome, email, senha) VALUES ('555.666.777-88', 'Ana Oliveira', 'ana@example.com', '123');

-- Inserções na tabela Funcionario
INSERT INTO Funcionario (cargo, id_gerente, cpf_pessoa) VALUES ('Gerente', NULL, '123.456.789-11');

INSERT INTO Funcionario (cargo, id_gerente, cpf_pessoa) VALUES ('Atendente', 1, '987.654.321-99');

-- Inserções na tabela Cliente
INSERT INTO Cliente (telefone, telefone2, rua, numero, cpf_pessoa_cliente) VALUES ('111-222-3333', NULL, 'Rua C', 789, '111.222.333-44');
INSERT INTO Cliente (telefone, telefone2, rua, numero, cpf_pessoa_cliente) VALUES ('555-666-7777', NULL, 'Rua D', 123, '555.666.777-88');


select * from cliente_faz_review_produto;

-- Inserções na tabela Transportadora
INSERT INTO Transportadora (id_transportadora, telefone, nome) VALUES (1, '555-123-4567', 'Transportadora A');
INSERT INTO Transportadora (id_transportadora, telefone, nome) VALUES (2, '555-987-6543', 'Transportadora B');

-- Inserções na tabela pedido
INSERT INTO pedido (status, data_compra, data_chegada, data_prevista, fk_cliente_id, fk_id_transportadora) VALUES ('Enviado', '2023-11-05', '2023-11-10', '2023-11-15', 1, 1);

INSERT INTO pedido (status, data_compra, data_chegada, data_prevista, fk_cliente_id, fk_id_transportadora) VALUES ('Processando', '2023-11-06', NULL, '2023-11-20', 2, 2);

-- Inserções na tabela review
INSERT INTO review (descricao, nota) VALUES ('Ótimo produto!', 5.0);
INSERT INTO review (descricao, nota) VALUES ('Bom serviço de entrega.', 4.0);

-- Inserções na tabela Categoria
INSERT INTO Categoria (id_categoria, nome) VALUES (1, 'Eletrônicos');
INSERT INTO Categoria (id_categoria, nome) VALUES (2, 'Roupas');

-- Inserções na tabela Produto
INSERT INTO Produto (id_produto, nome, preco, descricao, fk_id_categoria, image) VALUES (1, 'Smartphone', 599.99, 'Um smartphone de última geração', 1, '/images/iphone.jpg');
INSERT INTO Produto (id_produto, nome, preco, descricao, fk_id_categoria, image) VALUES (2, 'Camiseta', 19.99, 'Camiseta de algodão', 2,  '/images/camisa.jpg');
INSERT INTO Produto (id_produto, nome, preco, descricao, fk_id_categoria, image) VALUES (4, 'Casaco', 20, 'Casaco de linho', 2,  '/images/casaco.jpg');

INSERT INTO Produto (id_produto, nome, preco, descricao, fk_id_categoria, image) VALUES (3, 'Laptop', 999.99, 'High-performance laptop', 1,  '/images/laptop.jpg');

-- Inserções na tabela cliente_faz_review_produto
INSERT INTO cliente_faz_review_produto (fk_id_cliente, fk_id_review, fk_id_produto) VALUES (1, 1, 1);

INSERT INTO cliente_faz_review_produto (fk_id_cliente, fk_id_review, fk_id_produto) VALUES (2, 2, 2);

-- Inserções na tabela Responde
INSERT INTO Responde (fk_id_funcionario, fk_id_review, resposta) VALUES (1, 1, 'Agradecemos pelo seu feedback!');

INSERT INTO Responde (fk_id_funcionario, fk_id_review, resposta) VALUES (2, 2, 'Estamos trabalhando para melhorar nossas entregas.');

-- Inserções na tabela fornecedor
INSERT INTO fornecedor (id_fornecedor, nome, telefone) VALUES (1, 'Fornecedor A', '555-111-2222');
INSERT INTO fornecedor (id_fornecedor, nome, telefone) VALUES (2, 'Fornecedor B', '555-333-4444');

-- Inserções na tabela Possui
INSERT INTO Possui (fk_id_produto, fk_id_fornecedor) VALUES (1, 1);
INSERT INTO Possui (fk_id_produto, fk_id_fornecedor) VALUES (2, 2);

INSERT INTO pedido (status, data_compra, data_prevista, fk_cliente_id, fk_id_transportadora)
VALUES (
    'Em processamento',
    CURRENT_DATE,
    DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY),
    id_cliente,
    id_transport
);

-- Inserções na tabela Contem
INSERT INTO Contem (fk_id_produto, fk_id_pedido, quantidade) VALUES (1, 2, 2);
INSERT INTO Contem (fk_id_produto, fk_id_pedido, quantidade) VALUES (2, 4, 3);
INSERT INTO Contem (fk_id_produto, fk_id_pedido, quantidade) VALUES (3, 3, 2);

-- Produtos com maior nota:
SELECT p.id_produto, p.nome, AVG(r.nota) as media_nota
FROM Produto p
JOIN cliente_faz_review_produto cf ON p.id_produto = cf.fk_id_produto
JOIN Review r ON cf.fk_id_review = r.id_review
GROUP BY p.id_produto
ORDER BY media_nota DESC
LIMIT 1;

-- Produtos com mais avaliações e a sua nota
SELECT p.id_produto, p.nome, COUNT(r.id_review) as num_avaliacoes, AVG(r.nota) as media_nota
FROM Produto p
JOIN cliente_faz_review_produto cf ON p.id_produto = cf.fk_id_produto
JOIN Review r ON cf.fk_id_review = r.id_review
GROUP BY p.id_produto
ORDER BY num_avaliacoes DESC, media_nota DESC
LIMIT 1;

-- Produto com mais comentários

SELECT p.id_produto, p.nome, COUNT(r.id_review) as num_comentarios
FROM Produto p
JOIN cliente_faz_review_produto cf ON p.id_produto = cf.fk_id_produto
JOIN Review r ON cf.fk_id_review = r.id_review
GROUP BY p.id_produto
ORDER BY num_comentarios DESC
LIMIT 1;

-- Funcionário que mais respondeu

SELECT f.id_funcionario,p.nome, COUNT(r.fk_id_review) as num_respostas
FROM Funcionario f
join pessoa p on f.cpf_pessoa = p.cpf 
JOIN Responde r ON f.id_funcionario = r.fk_id_funcionario
GROUP BY f.id_funcionario
ORDER BY num_respostas DESC
LIMIT 1;

-- Produto com mais pedidos

SELECT p.id_produto, p.nome, COUNT(c.fk_id_pedido) as num_pedidos
FROM Produto p
JOIN Contem c ON p.id_produto = c.fk_id_produto
GROUP BY p.id_produto
ORDER BY num_pedidos DESC
LIMIT 1;

-- Cliente que mais realizou comentários

SELECT c.id_cliente,p.nome, COUNT(r.id_review) as num_comentarios
FROM Cliente c
join Pessoa p on c.cpf_pessoa_cliente  = p.cpf 
JOIN cliente_faz_review_produto cf ON c.id_cliente = cf.fk_id_cliente
JOIN Review r ON cf.fk_id_review = r.id_review
GROUP BY c.id_cliente
ORDER BY num_comentarios DESC
LIMIT 1;

-- Cliente que mais realizou pedidos

SELECT c.id_cliente, COUNT(p.id_pedido) as num_pedidos
FROM Cliente c
JOIN Pedido p ON c.id_cliente = p.fk_cliente_id
GROUP BY c.id_cliente
ORDER BY num_pedidos DESC
LIMIT 1;

-- Transportadora mais escolhida

SELECT t.id_transportadora, t.nome, COUNT(p.id_pedido) as num_escolhas
FROM Transportadora t
JOIN Pedido p ON t.id_transportadora = p.fk_id_transportadora
GROUP BY t.id_transportadora
ORDER BY num_escolhas DESC
LIMIT 1;

-- Transportadora que mais entregou dentro do prazo

SELECT t.id_transportadora, t.nome, COUNT(p.id_pedido) as entregas_no_prazo
FROM Transportadora t
JOIN Pedido p ON t.id_transportadora = p.fk_id_transportadora
WHERE p.data_chegada <= p.data_prevista
GROUP BY t.id_transportadora
ORDER BY entregas_no_prazo DESC
LIMIT 1;

select * from cliente;