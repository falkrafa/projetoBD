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

select * from funcionario f 

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


UPDATE pedido p SET p.status = 'Enviado', p.data_chegada = '2023-11-20' WHERE p.id_pedido = 10

select p.id_produto as idProduto, p.nome as nomeProduto, p.preco as PrecoProduto, p.descricao as DescricaoProduto, c.nome as NomeCategoria, p.image as Image1
from produto p
join categoria c on p.fk_id_categoria = c.id_categoria
where p.id_produto = 1

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

select p.id_produto as idProduto, p.nome as nomeProduto, p.preco as PrecoProduto, p.descricao as DescricaoProduto, p.image as image,p.image2 as image2, p.image3 as image3, c.nome as NomeCategoria
from produto p
join categoria c on p.fk_id_categoria = c.id_categoria
where p.id_produto = 1


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
telefone varchar(50) unique
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
INSERT INTO Produto (id_produto, nome, preco, descricao, fk_id_categoria, image, image2, image3) VALUES (1, 'Smartphone', 699.99, 'Good Smatphone', 1, '/images/iphone.jpg', '/images/iphone2.jpg', '/images/iphone3.jpg');
INSERT INTO Produto (id_produto, nome, preco, descricao, fk_id_categoria, image, image2, image3) VALUES (2, 'T-shirt', 19.99, 'Good T-shirt', 2,  '/images/camiseta.jpg','/images/camiset2.jpg','/images/camiseta3.jpg');
INSERT INTO Produto (id_produto, nome, preco, descricao, fk_id_categoria, image, image2, image3) VALUES (4, 'Hoodie', 39.99, 'Great Hoodie', 2,  '/images/casaco.jpg', '/images/casaco2.jpg', '/images/casaco3.jpg');

INSERT INTO Produto (id_produto, nome, preco, descricao, fk_id_categoria, image, image2, image3) VALUES (3, 'Laptop', 999.99, 'High-performance laptop', 1,  '/images/laptop.jpg', '/images/laptop2.jpg', '/images/laptop3.jpg');


-- Inserções na tabela cliente_faz_review_produto
INSERT INTO cliente_faz_review_produto (fk_id_cliente, fk_id_review, fk_id_produto) VALUES (1, 4, 1);

INSERT INTO cliente_faz_review_produto (fk_id_cliente, fk_id_review, fk_id_produto) VALUES (2, 30, 2);
INSERT INTO cliente_faz_review_produto (fk_id_cliente, fk_id_review, fk_id_produto) VALUES (1, 9, 1);

select * from review r  
select * from responde
-- Inserções na tabela Responde
INSERT INTO Responde (fk_id_funcionario, fk_id_review, resposta) VALUES (1, 30, 'Agradecemos pelo seu feedback!');

INSERT INTO Responde (fk_id_funcionario, fk_id_review, resposta) VALUES (2, 4, 'Obrigado!');
INSERT INTO Responde (fk_id_funcionario, fk_id_review, resposta) VALUES (2, 9, 'Agradeço!');

-- Inserções na tabela fornecedor
INSERT INTO fornecedor (id_fornecedor, nome, telefone) VALUES (1, 'Fornecedor A', '555-111-2222');
INSERT INTO fornecedor (id_fornecedor, nome, telefone) VALUES (2, 'Fornecedor B', '555-333-4444');

-- Inserções na tabela Possui
INSERT INTO Possui (fk_id_produto, fk_id_fornecedor) VALUES (1, 1);
INSERT INTO Possui (fk_id_produto, fk_id_fornecedor) VALUES (2, 2);


-- Inserções na tabela Contem
INSERT INTO Contem (fk_id_produto, fk_id_pedido, quantidade) VALUES (1, 2, 2);
INSERT INTO Contem (fk_id_produto, fk_id_pedido, quantidade) VALUES (2, 1, 3);
INSERT INTO Contem (fk_id_produto, fk_id_pedido, quantidade) VALUES (3, 6, 2);

-- More Pessoa (Person) entries
INSERT INTO Pessoa (cpf, nome, email, senha) VALUES ('333.444.555-66', 'Laura Oliveira', 'laura@example.com', '456');
INSERT INTO Pessoa (cpf, nome, email, senha) VALUES ('999.888.777-55', 'Pedro Lima', 'pedro@example.com', '789');

select * from pessoa

-- More Funcionario (Employee) entries
INSERT INTO Funcionario (cargo, id_gerente, cpf_pessoa) VALUES ('Atendente', 1, '333.444.555-66');
INSERT INTO Funcionario (cargo, id_gerente, cpf_pessoa) VALUES ('Atendente', 1, '999.888.777-55');

-- More Cliente (Client) entries
INSERT INTO Cliente (telefone, telefone2, rua, numero, cpf_pessoa_cliente) VALUES ('444-555-6666', NULL, 'Rua E', 456, '333.444.555-66');
INSERT INTO Cliente (telefone, telefone2, rua, numero, cpf_pessoa_cliente) VALUES ('777-888-9999', NULL, 'Rua F', 789, '999.888.777-55');

select * from categoria c 

-- More Transportadora (Carrier) entries
INSERT INTO Transportadora (id_transportadora, telefone, nome) VALUES (3, '555-111-3333', 'Transportadora C');
INSERT INTO Transportadora (id_transportadora, telefone, nome) VALUES (4, '555-999-8888', 'Transportadora D');


-- More Categoria (Category) entries
INSERT INTO Categoria (id_categoria, nome) VALUES (3, 'Calçado');
INSERT INTO Categoria (id_categoria, nome) VALUES (4, 'Acessórios');

select * from produto

-- More Produto (Product) entries
INSERT INTO Produto (id_produto, nome, preco, descricao, fk_id_categoria, image, image2, image3) VALUES (5, 'Nike Sneaker', 29.99, 'A very comfortable sneaker', 3, '/images/nike.jpg', '/images/nike2.jpg', '/images/nike3.jpg');
INSERT INTO Produto (id_produto, nome, preco, descricao, fk_id_categoria, image, image2, image3) VALUES (6, 'Sunglasses', 49.99, 'Stylish sunglasses', 4, '/images/oculos.jpg', '/images/oculos2.jpg', '/images/oculos3.jpg');

-- More Review entries
INSERT INTO review (descricao, nota) VALUES ('Produto excelente, recomendo!', 4.5);
INSERT INTO review (descricao, nota) VALUES ('Entrega rápida, ótimo serviço!', 5.0);

select * from funcionario f 

-- More cliente_faz_review_produto (Client makes product review) entries
INSERT INTO cliente_faz_review_produto (fk_id_cliente, fk_id_review, fk_id_produto) VALUES (6, 32, 5);
INSERT INTO cliente_faz_review_produto (fk_id_cliente, fk_id_review, fk_id_produto) VALUES (7, 33, 6);

-- More Responde (Responds) entries
INSERT INTO Responde (fk_id_funcionario, fk_id_review, resposta) VALUES (3, 32, 'Agradecemos pelo seu feedback positivo!');
INSERT INTO Responde (fk_id_funcionario, fk_id_review, resposta) VALUES (4, 33, 'Ficamos felizes em saber que você gostou do serviço.');

-- More fornecedor (Supplier) entries
INSERT INTO fornecedor (id_fornecedor, nome, telefone) VALUES (3, 'Fornecedor C', '555-777-8888');
INSERT INTO fornecedor (id_fornecedor, nome, telefone) VALUES (4, 'Fornecedor D', '555-222-3333');

-- More Possui (Owns) entries
INSERT INTO Possui (fk_id_produto, fk_id_fornecedor) VALUES (3, 3);
INSERT INTO Possui (fk_id_produto, fk_id_fornecedor) VALUES (4, 4);

-- More pedido entries
INSERT INTO pedido (status, data_compra, data_chegada, data_prevista, fk_cliente_id, fk_id_transportadora) VALUES ('Em processamento', '2023-11-12', NULL, '2023-11-25', 3, 3);
INSERT INTO pedido (status, data_compra, data_chegada, data_prevista, fk_cliente_id, fk_id_transportadora) VALUES ('Enviado', '2023-11-15', '2023-11-20', '2023-11-25', 4, 4);

-- More Contem (Contains) entries
select * from produto

INSERT INTO Contem (fk_id_produto, fk_id_pedido, quantidade) VALUES (5, 22, 1);
INSERT INTO Contem (fk_id_produto, fk_id_pedido, quantidade) VALUES (6, 23, 2);


