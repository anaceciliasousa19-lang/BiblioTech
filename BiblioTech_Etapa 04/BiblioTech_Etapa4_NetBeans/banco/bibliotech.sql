-- BIBLIOTECH - ETAPA 4
-- Execute este arquivo inteiro no MySQL Workbench.

CREATE DATABASE IF NOT EXISTS bibliotech
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE bibliotech;

CREATE TABLE IF NOT EXISTS administradores (
    id_admin INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(80) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS livros (
    id_livro INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(180) NOT NULL,
    autor VARCHAR(180) NOT NULL,
    ano INT NOT NULL,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    editora VARCHAR(150) DEFAULT '',
    isbn VARCHAR(40) NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(160) NOT NULL,
    matricula VARCHAR(30) NULL UNIQUE,
    contato VARCHAR(160) NOT NULL,
    telefone VARCHAR(30) DEFAULT '',
    situacao VARCHAR(20) NOT NULL DEFAULT 'Regular'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS emprestimos (
    id_emprestimo INT AUTO_INCREMENT PRIMARY KEY,
    id_livro INT NOT NULL,
    id_usuario INT NOT NULL,
    data_emprestimo DATE NOT NULL,
    data_devolucao DATE NOT NULL,
    data_devolucao_real DATE NULL,
    CONSTRAINT fk_emp_livro FOREIGN KEY (id_livro) REFERENCES livros(id_livro),
    CONSTRAINT fk_emp_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    INDEX idx_emp_livro (id_livro),
    INDEX idx_emp_usuario (id_usuario),
    INDEX idx_emp_devolucao (data_devolucao, data_devolucao_real)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO administradores (id_admin,usuario,email,senha)
VALUES (1,'admin','admin@bibliotech.com','1234');

INSERT IGNORE INTO livros (id_livro,titulo,autor,ano,disponivel,editora,isbn) VALUES
(1001,'O Senhor dos Anéis','J.R.R. Tolkien',1954,TRUE,'HarperCollins','978-0007525546'),
(1002,'Harry Potter','J.K. Rowling',1997,FALSE,'Rocco','978-8532511010'),
(1003,'1984','George Orwell',1949,TRUE,'Companhia das Letras','978-8535914849'),
(1004,'O Pequeno Príncipe','Antoine de Saint-Exupéry',1943,TRUE,'Agir','978-8522005239'),
(1005,'Dom Casmurro','Machado de Assis',1899,FALSE,'Ática','978-8508142567'),
(1006,'A Revolução dos Bichos','George Orwell',1945,TRUE,'Companhia das Letras','978-8535909555'),
(1007,'Cem Anos de Solidão','Gabriel García Márquez',1967,TRUE,'Record','978-8501012078'),
(1008,'O Código Da Vinci','Dan Brown',2003,FALSE,'Sextante','978-8599296158');

INSERT IGNORE INTO usuarios (id_usuario,nome,matricula,contato,telefone,situacao) VALUES
(2101,'João Silva','2026001','joao.silva@email.com','(11) 99999-1111','Pendente'),
(2102,'Maria Souza','2026002','maria.souza@email.com','(11) 98765-4321','Regular'),
(2103,'Carlos Oliveira','2026003','carlos.oliveira@email.com','(21) 98888-3333','Bloqueado'),
(2104,'Ana Santos','2026004','ana.santos@email.com','(21) 91234-5678','Regular'),
(2105,'Pedro Costa','2026005','pedro.costa@email.com','(85) 97777-5555','Regular');

INSERT IGNORE INTO emprestimos
(id_emprestimo,id_livro,id_usuario,data_emprestimo,data_devolucao,data_devolucao_real) VALUES
(1,1005,2101,DATE_SUB(CURDATE(), INTERVAL 20 DAY),DATE_SUB(CURDATE(), INTERVAL 5 DAY),NULL),
(2,1008,2102,DATE_SUB(CURDATE(), INTERVAL 4 DAY),DATE_ADD(CURDATE(), INTERVAL 10 DAY),NULL),
(3,1002,2103,DATE_SUB(CURDATE(), INTERVAL 18 DAY),DATE_SUB(CURDATE(), INTERVAL 3 DAY),NULL);

UPDATE livros SET disponivel=FALSE WHERE id_livro IN (1002,1005,1008);

-- Consultas de conferência
SELECT * FROM livros ORDER BY id_livro;
SELECT * FROM usuarios ORDER BY id_usuario;
SELECT * FROM emprestimos ORDER BY id_emprestimo;
