USE clinica_medica;

-- PROFISSIONAIS DE SAUDE

INSERT INTO Profissional_Saude (nome, cpf, data_nascimento) 

VALUES
('Lucas Almeida', '58317492061', '1980-03-15'),
('Mariana Costa', '29468173052', '1985-07-22'),
('Rafael Martins', '76139584027', '1978-11-09'),
('Camila Ferreira', '43872615093', '1990-01-30'),
('Gabriel Rocha', '61528397041', '1983-05-18'),
('Juliana Mendes', '32759486015', '1988-09-12'),
('Felipe Nogueira', '85241763029', '1975-12-03'),
('Beatriz Carvalho', '46913572084', '1992-04-25'),
('Andre Ribeiro', '73182645037', '1981-08-14'),
('Larissa Oliveira', '24896357016', '1987-02-07'),
('Thiago Barros', '59627184032', '1979-06-21'),
('Renata Freitas', '81345962075', '1991-10-16'),
('Eduardo Lima', '37592841063', '1984-01-11'),
('Patricia Gomes', '62481735092', '1986-05-29'),
('Ana Paula Silva', '45937268021', '1993-03-08'),
('Carlos Henrique', '78251493046', '1989-07-17'),
('Debora Santos', '31684957028', '1994-12-01'),
('Marcelo Vieira', '54726381095', '1982-09-26'),
('Fernanda Dias', '89143562073', '1990-06-05'),
('Joao Pedro Moraes', '26375849017', '1988-11-19');


-- MEDICOS

INSERT INTO Medico (id_profissional, crm, id_supervisor) 

VALUES
(1,  'CRM-DF 18452', NULL),
(2,  'CRM-DF 23791', NULL),
(3,  'CRM-DF 15684', NULL),
(4,  'CRM-DF 29417', NULL),
(5,  'CRM-DF 21356', NULL),
(6,  'CRM-DF 26843', 1),
(7,  'CRM-DF 19725', 2),
(8,  'CRM-DF 31568', 3),
(9,  'CRM-DF 22479', 1),
(10, 'CRM-DF 28314', 2),
(11, 'CRM-DF 17693', 3),
(12, 'CRM-DF 30241', 4),
(13, 'CRM-DF 25187', 5),
(14, 'CRM-DF 32954', 5);


-- ENFERMEIROS

INSERT INTO Enfermeiro (id_profissional, coren) VALUES
(15, 'COREN-DF 184527'),
(16, 'COREN-DF 239814'),
(17, 'COREN-DF 315692'),
(18, 'COREN-DF 276451'),
(19, 'COREN-DF 198374'),
(20, 'COREN-DF 342816');

-- ESPECIALIDADES

INSERT INTO Especialidade (nome) VALUES
('Cardiologia'),
('Pediatria'),
('Dermatologia'),
('Neurologia'),
('Ortopedia'),
('Psiquiatria'),
('Ginecologia'),
('Endocrinologia'),
('Oftalmologia'),
('Clinica Geral');


-- MEDICO_ESPECIALIDADE

INSERT INTO Medico_Especialidade
(id_medico, id_especialidade, data_obtencao) VALUES
(1,  1, '2010-03-15'),
(1,  10, '2008-12-10'),

(2,  2, '2014-07-20'),
(2,  10, '2012-05-08'),

(3,  3, '2007-11-18'),
(3,  10, '2005-09-14'),

(4,  4, '2018-02-25'),
(4,  10, '2016-06-30'),

(5,  5, '2011-08-12'),
(5,  10, '2009-03-22'),

(6,  6, '2017-04-10'),
(6,  10, '2015-01-19'),

(7,  7, '2006-09-23'),
(7,  10, '2004-12-15'),

(8,  8, '2020-05-17'),
(8,  10, '2018-11-03'),

(9,  9, '2013-10-06'),
(9,  10, '2011-04-28'),

(10, 1, '2016-01-14'),
(11, 2, '2009-07-30'),
(12, 4, '2019-09-11'),
(12, 10, '2017-02-18'),
(13, 5, '2012-06-24'),
(14, 7, '2015-08-05'),
(14, 10, '2013-03-27');


-- MEDICAMENTOS

INSERT INTO Medicamento (nome) VALUES
('Dipirona'),
('Paracetamol'),
('Ibuprofeno'),
('Amoxicilina'),
('Azitromicina'),
('Losartana'),
('Metformina'),
('Omeprazol'),
('Loratadina'),
('Prednisona'),
('Sinvastatina'),
('Enalapril'),
('Atenolol'),
('Fluoxetina'),
('Sertralina'),
('Clonazepam'),
('Amoxicilina com Clavulanato'),
('Salbutamol'),
('Cetirizina'),
('Diclofenaco');
