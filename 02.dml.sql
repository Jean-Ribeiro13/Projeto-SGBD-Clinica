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
(id_medico, id_especialidade, data_obtencao) 
VALUES
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

INSERT INTO Medicamento (nome) 
VALUES
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

-- PACIENTES

INSERT INTO Paciente (nome, cpf, data_nascimento)
VALUES
('Arthur Monteiro','58317492061', '1998-02-14'),
('Helena Duarte','29468173052', '2001-07-23'),
('Bruno Tavares',  '76139584027', '1987-11-05'),
('Isabela Freire', '43872615093', '1995-04-18'),
('Matheus Cardoso','61528397041', '1979-09-30'),
('Sofia Azevedo', '32759486015', '2003-01-12'),
('Diego Moreira', '85241763029', '1991-06-27'),
('Amanda Teixeira', '46913572084', '1984-12-09'),
('Leonardo Campos','73182645037', '1997-03-21'),
('Bianca Martins', '24896357016', '2000-08-16'),
('Gustavo Peixoto','59627184032', '1976-05-11'),
('Natalia Correia', '81345962075', '1993-10-24'),
('Vinicius Andrade','37592841063', '1989-02-07'),
('Carolina Farias', '62481735092', '2002-11-19'),
('Rodrigo Bastos',  '45937268021', '1981-07-04'),
('Leticia Vasconcelos','78251493046', '1996-09-13'),
('Henrique Melo', '31684957028', '1974-01-28'),
('Gabriela Pires', '54726381095', '2004-06-02'),
('Caio Rezende','89143562073', '1990-12-17'),
('Manuela Prado', '26375849017', '1986-03-25'),
('Murilo Assis', '71493628051', '1999-05-08'),
('Clara Medeiros', '38512764092', '2001-02-20'),
('Samuel Dantas',  '92746135018', '1983-08-31'),
('Valentina Cunha', '54178326049', '1994-10-06'),
('Igor Neves', '16842975036', '1977-04-15'),
('Alice Queiroz', '67381524097', '2005-09-22'),
('Daniel Borges',  '42957613082', '1988-06-13'),
('Laura Macedo',  '85623471059', '1992-01-03'),
('Pedro Viana', '31274896025', '1980-11-26'),
('Luiza Amaral', '79536142084', '1997-07-10'),
('Nathan Moura', '24681957031', '1985-02-19'),
('Cecilia Paiva', '91827364052', '2000-04-29'),
('Victor Siqueira', '36492571086', '1991-12-08'),
('Melissa Coelho', '57184263019', '2003-03-16'),
('Alexandre Fonseca', '82315649027', '1973-06-21'),
('Marina Sales', '45729186034', '1996-08-14'),
('Otavio Guimaraes', '69143825072', '1982-10-01'),
('Elisa Torres', '23576491058', '1999-01-27'),
('Renan Lopes', '78451236091', '1987-05-18'),
('Heloisa Maia', '51963824076', NULL);

-- CONTATOS DE EMERGENCIA

INSERT INTO Contato_Emergencia
(id_paciente, nome, telefone, parentesco)
VALUES
(1, 'Marcos Monteiro', '61991110001', 'Pai'),
(1, 'Luciana Monteiro', '61991110002', 'Mae'),
(2, 'Paulo Duarte', '61991110003', 'Pai'),
(3, 'Renata Tavares', '61991110004', 'Irma'),
(4, 'Carlos Freire', '61991110005', 'Pai'),
(5, 'Fernanda Cardoso', '61991110006', 'Esposa'),
(6, 'Ricardo Azevedo', '61991110007', 'Pai'),
(6, 'Patricia Azevedo','61991110008', 'Mae'),
(7, 'Juliana Moreira', '61991110009', 'Esposa'),
(8, 'Roberto Teixeira', '61991110010', 'Marido'),
(9, 'Camila Campos', '61991110011', 'Irma'),
(10, 'Marcelo Martins', '61991110012', 'Pai'),
(11, 'Sandra Peixoto', '61991110013', 'Esposa'),
(12, 'Eduardo Correia', '61991110014', 'Irmao'),
(13, 'Marcia Andrade', '61991110015', 'Mae'),
(14, 'Fabio Farias', '61991110016', 'Pai'),
(15, 'Luciana Bastos','61991110017', 'Esposa'),
(15, 'Henrique Bastos', '61991110018', 'Filho'),
(16, 'Antonio Vasconcelos','61991110019', 'Pai'),
(17, 'Paula Melo', '61991110020', 'Filha'),
(18, 'Rodrigo Pires', '61991110021', 'Irmao'),
(19, 'Daniel Rezende', '61991110022', 'Pai'),
(20, 'Monica Prado', '61991110023', 'Mae'),
(21, 'Renato Assis', '61991110024', 'Pai'),
(22, 'Helena Medeiros', '61991110025', 'Mae'),
(23, 'Lucia Dantas', '61991110026', 'Esposa'),
(24, 'Rafael Cunha',  '61991110027', 'Pai'),
(25, 'Simone Neves', '61991110028', 'Esposa'),
(26, 'Carlos Queiroz', '61991110029', 'Pai'),
(27, 'Amanda Borges', '61991110030', 'Esposa'),
(28, 'Marcelo Macedo', '61991110031', 'Irmao'),
(29, 'Cristina Viana', '61991110032', 'Esposa'),
(30, 'Fernando Amaral','61991110033', 'Pai'),
(31, 'Lucia Moura', '61991110034', 'Esposa'),
(32, 'Roberto Paiva', '61991110035', 'Pai'),
(33, 'Mariana Siqueira', '61991110036', NULL),
(34, 'Henrique Coelho', '61991110037', 'Pai'),
(35, 'Patricia Fonseca', '61991110038', 'Esposa'),
(36, 'Carlos Sales', '61991110039', 'Pai'),
(37, 'Luciana Guimaraes','61991110040', 'Esposa'),
(38, 'Rafael Torres', '61991110041', 'Irmao'),
(38, 'Vanessa Torres','61991110042', 'Irma'),
(39, 'Marcia Lopes',  '61991110043', 'Mae');

-- PRONTUARIOS

INSERT INTO Prontuario
(id_paciente, tipo_sanguineo)
VALUES
(1,  'A+'),
(2,  'O+'),
(3,  'B+'),
(4,  'AB+'),
(5,  'A-'),
(6,  'O-'),
(7,  'B-'),
(8,  'AB-'),
(9,  'A+'),
(10, 'O+'),
(11, 'B+'),
(12, 'AB+'),
(13, 'A-'),
(14, 'O-'),
(15, 'B-'),
(16, 'AB-'),
(17, 'A+'),
(18, 'O+'),
(19, 'B+'),
(20, 'AB+'),
(21, 'A-'),
(22, 'O-'),
(23, 'B-'),
(24, 'AB-'),
(25, 'A+'),
(26, 'O+'),
(27, 'B+'),
(28, 'AB+'),
(29, 'A-'),
(30, 'O-'),
(31, 'B-'),
(32, 'AB-'),
(33, 'A+'),
(34, NULL),
(35, 'O+'),
(36, 'B+');

-- EVOLUCOES CLINICAS

INSERT INTO Evolucao_Clinica
(id_prontuario, id_profissional, data_ocorrencia, descricao)
VALUES
-- PRONTUARIO 1
(1, 1, '2024-02-10 09:15:00', 'Paciente apresentou pressao arterial elevada durante avaliacao inicial.'),
(1, 15, '2024-06-18 14:20:00', 'Paciente orientado sobre controle de pressao arterial e acompanhamento regular.'),
(1, 1, '2025-01-22 10:10:00', 'Pressão arterial apresentou melhora apos acompanhamento clinico.'),
-- PRONTUARIO 2
(2, 2, '2024-01-12 08:30:00', 'Paciente apresentou quadro de febre e dor de garganta.'),
(2, 16, '2024-01-15 11:40:00', 'Paciente relatou reducao da febre e melhora do estado geral.'),
(2, 2, '2024-01-20 09:25:00', 'Quadro infeccioso resolvido sem intercorrencias.'),
-- PRONTUARIO 3
(3, 3, '2024-03-05 13:10:00', 'Paciente apresentou lesoes avermelhadas na pele com prurido.'),
(3, 17, '2024-03-12 15:45:00', 'Paciente relatou diminuicao do prurido apos inicio do tratamento.'),
(3, 3, '2024-03-28 10:30:00', 'Lesões cutaneas apresentaram regressao significativa.'),
-- PRONTUARIO 4
(4, 4, '2024-04-11 09:00:00', 'Paciente relatou episodios recorrentes de dor de cabeca.'),
(4, 18, '2024-04-20 16:15:00', 'Paciente manteve registro da frequencia das cefaleias conforme orientacao.'),
(4, 4, '2024-05-03 08:50:00', 'Paciente relatou reducao da frequencia das cefaleias.'),
-- PRONTUARIO 5
(5, 5, '2024-05-14 10:40:00', 'Paciente apresentou dor no joelho direito apos atividade fisica.'),
(5, 19, '2024-05-22 13:35:00', 'Paciente apresentou melhora parcial da dor e da mobilidade.'),
(5, 5, '2024-06-04 09:20:00', 'Paciente apresentou recuperacao funcional satisfatoria do joelho.'),
-- PRONTUARIO 6
(6, 6, '2024-06-03 14:00:00', 'Paciente relatou ansiedade frequente e dificuldade para dormir.'),
(6, 20, '2024-06-17 10:15:00', 'Paciente relatou melhora discreta na qualidade do sono.'),
(6, 6, '2024-07-08 15:30:00', 'Paciente apresentou reducao dos sintomas de ansiedade.'),
-- PRONTUARIO 7
(7, 7, '2024-07-02 09:40:00', 'Paciente compareceu para avaliacão ginecologica de rotina.'),
(7, 15, '2024-07-10 11:25:00', 'Paciente orientada sobre exames preventivos periodicos.'),
(7, 7, '2024-07-25 08:45:00', 'Resultados dos exames analisados sem alteracoes relevantes.'),
-- PRONTUARIO 8
(8, 8, '2024-08-06 13:20:00', 'Paciente apresentou alteracao glicemica em exame de rotina.'),
(8, 16, '2024-08-15 09:35:00', 'Paciente recebeu orientacoes sobre alimentacao e controle glicemico.'),
(8, 8, '2024-09-10 14:10:00', 'Exames demonstraram melhora nos niveis glicemicos.'),
-- PRONTUARIO 9
(9, 9, '2024-09-04 10:00:00', 'Paciente relatou dificuldade de visao para objetos distantes.'),
(9, 17, '2024-09-12 15:05:00', 'Paciente realizou avaliacao visual complementar.'),
(9, 9, '2024-09-26 11:30:00', 'Paciente adaptado a correcao visual prescrita.'),
-- PRONTUARIO 10
(10, 10, '2024-10-01 08:20:00', 'Paciente apresentou sintomas gripais leves.'),
(10, 18, '2024-10-05 13:45:00', 'Paciente relatou melhora da congestao nasal e da tosse.'),
(10, 10, '2024-10-12 09:10:00', 'Paciente encontra-se sem sintomas respiratorios.'),
-- PRONTUARIO 11
(11, 1, '2024-11-07 10:25:00', 'Paciente apresentou palpitações ocasionais durante esforco fisico.'),
(11, 19, '2024-11-14 14:30:00', 'Paciente permaneceu em observacao sem novas intercorrencias.'),
(11, 1, '2024-12-03 11:00:00', 'Avaliacao cardiovascular apresentou evolucao estavel.'),
-- PRONTUARIO 12
(12, 2, '2024-12-02 09:15:00', 'Paciente apresentou tosse persistente e febre baixa.'),
(12, 20, '2024-12-06 15:20:00', 'Paciente relatou melhora parcial dos sintomas respiratorios.'),
(12, 2, '2024-12-15 10:40:00', 'Paciente apresentou resolucao do quadro respiratorio.'),
-- PRONTUARIO 13
(13, 3, '2025-01-08 13:25:00', 'Paciente apresentou irritacao cutanea nos membros superiores.'),
(13, 15, '2025-01-15 09:50:00', 'Irritacao apresentou melhora apos cuidados locais.'),
(13, 3, '2025-01-29 14:35:00', 'Pele sem sinais de inflamacao ativa.'),
-- PRONTUARIO 14
(14, 4, '2025-02-04 10:05:00', 'Paciente relatou episodios de tontura ao levantar-se rapidamente.'),
(14, 16, '2025-02-11 15:30:00', 'Paciente orientado sobre hidratacao e mudancas posturais graduais.'),
(14, 4, '2025-02-25 09:20:00', 'Paciente relatou reducao significativa dos episodios de tontura.'),
-- PRONTUARIO 15
(15, 5, '2025-03-06 08:40:00', 'Paciente apresentou dor lombar apos esforco fisico intenso.'),
(15, 17, '2025-03-13 13:15:00', 'Paciente relatou melhora da dor com medidas conservadoras.'),
(15, 5, '2025-03-27 10:30:00', 'Mobilidade lombar recuperada sem limitacoes importantes.'),
-- PRONTUARIO 16
(16, 6, '2025-04-02 14:10:00', 'Paciente relatou alteracoes de humor e dificuldade de concentracao.'),
(16, 18, '2025-04-16 09:45:00', 'Paciente relatou melhora gradual da rotina de sono.'),
(16, 6, '2025-05-07 15:20:00', 'Paciente apresentou melhora dos sintomas relatados inicialmente.'),
-- PRONTUARIO 17
(17, 7, '2025-05-05 10:20:00', 'Paciente realizou acompanhamento ginecologico preventivo.'),
(17, 19, '2025-05-12 14:05:00', 'Paciente realizou exames laboratoriais solicitados.'),
(17, 7, '2025-05-26 09:30:00', 'Exames analisados sem alteracoes clinicas significativas.'),
-- PRONTUARIO 18
(18, 8, '2025-06-03 08:50:00', 'Paciente apresentou aumento discreto da glicemia em jejum.'),
(18, 20, '2025-06-17 13:40:00', 'Paciente relatou adesao as orientacoes alimentares.'),
(18, 8, '2025-07-01 10:15:00', 'Controle glicemico apresentou evolucao favoravel.'),
-- PRONTUARIO 19
(19, 9, '2025-07-07 14:30:00', 'Paciente apresentou ardencia ocular e sensibilidade a luz.'),
(19, 15, '2025-07-14 09:55:00', 'Paciente relatou reducao da irritacao ocular.'),
(19, 9, '2025-07-28 11:20:00', 'Paciente sem sinais de irritacao ocular no retorno.'),
-- PRONTUARIO 20
(20, 10, '2025-08-04 09:10:00', 'Paciente apresentou dor abdominal leve apos alimentacao.'),
(20, 16, '2025-08-11 15:35:00', 'Paciente relatou melhora apos ajustes alimentares.'),
(20, 10, '2025-08-25 10:45:00', 'Paciente encontra-se assintomatico no retorno.'),
-- PRONTUARIO 21
(21, 1, '2025-09-02 13:00:00', 'Paciente apresentou pressao arterial acima dos valores habituais.'),
(21, 17, '2025-09-09 09:25:00', 'Pressao arterial monitorada durante acompanhamento.'),
(21, 1, '2025-09-23 14:40:00', 'Paciente apresentou estabilizacao dos niveis pressoricos.'),
-- PRONTUARIO 22
(22, 2, '2025-10-06 08:30:00', 'Paciente apresentou febre, congestao nasal e mal-estar.'),
(22, 18, '2025-10-10 13:50:00', 'Paciente relatou melhora progressiva dos sintomas.'),
(22, 2, '2025-10-17 09:15:00', 'Quadro infeccioso resolvido.'),
-- PRONTUARIO 23
(23, 3, '2025-11-03 10:20:00', 'Paciente apresentou ressecamento e descamacao da pele.'),
(23, 19, '2025-11-10 15:10:00', 'Paciente relatou melhora da hidratacao cutanea.'),
(23, 3, '2025-11-24 11:40:00', 'Pele apresentou aspecto normal no retorno.'),
-- PRONTUARIO 24
(24, 4, '2025-12-01 09:35:00', 'Paciente apresentou dormencia ocasional nas extremidades.'),
(24, 20, '2025-12-08 14:25:00', 'Paciente permaneceu sem piora dos sintomas.'),
(24, 4, '2025-12-22 10:05:00', 'Sintomas apresentaram reducao durante acompanhamento.'),
-- PRONTUARIO 25
(25, 5, '2026-01-05 08:45:00', 'Paciente apresentou dor no ombro direito associada a movimento.'),
(25, 15, '2026-01-12 13:30:00', 'Paciente relatou melhora parcial da mobilidade.'),
(25, 5, '2026-01-26 09:55:00', 'Paciente recuperou amplitude de movimento satisfatoria.'),
-- PRONTUARIO 26
(26, 6, '2026-02-02 14:15:00', 'Paciente relatou dificuldade para iniciar o sono e preocupacao excessiva.'),
(26, 16, '2026-02-16 10:30:00', 'Paciente relatou melhora parcial do padrao de sono.'),
(26, 6, '2026-03-02 15:05:00', 'Sintomas de ansiedade apresentaram reducao.'),
-- PRONTUARIO 27
(27, 7, '2026-03-04 09:20:00', 'Paciente compareceu para acompanhamento preventivo ginecologico.'),
(27, 17, '2026-03-11 14:00:00', 'Exames preventivos realizados conforme orientacao.'),
(27, 7, '2026-03-25 10:25:00', 'Resultados avaliados sem alteracoes relevantes.'),
-- PRONTUARIO 28
(28, 8, '2026-04-06 08:35:00', 'Paciente apresentou alteracao em exames de funcao metabolica.'),
(28, 18, '2026-04-13 13:20:00', 'Paciente recebeu orientacoes para mudancas alimentares.'),
(28, 8, '2026-04-27 09:45:00', 'Exames de controle apresentaram melhora.'),
-- PRONTUARIO 29
(29, 9, '2026-05-04 14:05:00', 'Paciente apresentou dificuldade de leitura em curtas distancias.'),
(29, 19, '2026-05-11 10:40:00', 'Paciente realizou avaliacao oftalmologica complementar.'),
(29, 9, '2026-05-25 15:15:00', 'Paciente apresentou boa adaptacao a correcao visual.'),
-- PRONTUARIO 30
(30, 10, '2026-06-01 09:00:00', 'Paciente apresentou quadro de indisposicao gastrointestinal leve.'),
(30, 20, '2026-06-05 14:30:00', 'Paciente relatou melhora apos hidratacao e dieta leve.'),
(30, 10, '2026-06-12 10:20:00', 'Paciente encontra-se sem sintomas gastrointestinais.'),
-- PRONTUARIO 31
(31, 1, '2026-06-08 08:40:00', 'Paciente relatou cansaco ao realizar esforcos moderados.'),
(31, 15, '2026-06-15 13:55:00', 'Paciente realizou acompanhamento dos sinais vitais.'),
(31, 1, '2026-06-29 09:30:00', 'Paciente apresentou melhora do condicionamento geral.'),
-- PRONTUARIO 32
(32, 2, '2026-07-01 10:10:00', 'Paciente apresentou sintomas respiratorios leves.'),
(32, 16, '2026-07-06 15:25:00', 'Paciente relatou reducao da tosse e congestao.'),
(32, 2, '2026-07-13 11:05:00', 'Paciente encontra-se recuperado do quadro respiratorio.'),
-- PRONTUARIO 33
(33, 3, '2026-07-08 09:50:00', 'Paciente apresentou coceira e vermelhidao em regiao cervical.'),
(33, 17, '2026-07-15 14:35:00', 'Paciente relatou reducao da irritacao cutanea.'),
(33, 3, '2026-07-29 10:15:00', 'Lesao cutanea resolvida sem complicacoes.'),
-- PRONTUARIO 34
(34, 4, '2026-08-03 08:30:00', 'Paciente relatou dor de cabeca acompanhada de sensibilidade a luz.'),
(34, 18, '2026-08-10 13:45:00', 'Paciente relatou menor intensidade das crises.'),
(34, 4, '2026-08-24 09:40:00', 'Paciente apresentou evolucao clinica favoravel.'),
-- PRONTUARIO 35
(35, 5, '2026-08-05 10:35:00', 'Paciente apresentou dor no tornozelo apos atividade esportiva.'),
(35, 19, '2026-08-12 15:10:00', 'Paciente apresentou reducao do edema e da dor.'),
(35, 5, '2026-08-26 11:25:00', 'Paciente recuperou mobilidade sem limitacao significativa.'),
-- PRONTUARIO 36
(36, 6, '2026-08-07 09:05:00', 'Paciente relatou aumento recente dos niveis de estresse.'),
(36, 20, '2026-08-14 14:20:00', 'Paciente relatou melhora do sono apos orientacoes.'),
(36, 6, '2026-08-28 10:50:00', 'Paciente apresentou melhora geral dos sintomas relatados.');

-- CONSULTAS

INSERT INTO Consulta
(id_paciente, id_medico, data_hora, valor)
VALUES
(1, 1, '2026-01-05 08:00:00', 250.00),
(2, 2, '2026-01-05 09:00:00', 180.00),
(3, 3, '2026-01-06 10:00:00', 220.00),
(4, 4, '2026-01-07 14:00:00', 280.00),
(5, 5, '2026-01-08 15:00:00', 230.00),
(6, 6, '2026-01-09 09:30:00', 200.00),
(7, 7, '2026-01-12 08:30:00', 240.00),
(8, 8, '2026-01-13 10:30:00', 260.00),
(9, 9, '2026-01-14 13:30:00', 210.00),
(10, 10, '2026-01-15 16:00:00', 150.00),
(11, 11, '2026-02-02 08:00:00', 180.00),
(12, 12, '2026-02-03 09:00:00', 280.00),
(13, 13, '2026-02-04 10:00:00', 230.00),
(14, 14, '2026-02-05 11:00:00', 240.00),
(15, 1, '2026-02-06 14:00:00', 250.00),
(16, 2, '2026-02-09 15:00:00', 180.00),
(17, 3, '2026-02-10 08:30:00', 220.00),
(18, 4, '2026-02-11 09:30:00', 280.00),
(19, 5, '2026-02-12 13:00:00', 230.00),
(20, 6, '2026-02-13 14:30:00', 200.00),
(21, 7, '2026-03-02 08:00:00', 240.00),
(22, 8, '2026-03-03 09:15:00', 260.00),
(23, 9, '2026-03-04 10:30:00', 210.00),
(24, 10, '2026-03-05 14:00:00', 150.00),
(25, 11, '2026-03-06 15:30:00', 180.00),
(26, 12, '2026-03-09 08:45:00', 280.00),
(27, 13, '2026-03-10 10:00:00', 230.00),
(28, 14, '2026-03-11 11:15:00', 240.00),
(29, 1, '2026-03-12 13:30:00', 250.00),
(30, 2, '2026-03-13 16:00:00', 180.00),
(31, 3, '2026-04-06 08:00:00', 220.00),
(32, 4, '2026-04-07 09:00:00', 280.00),
(33, 5, '2026-04-08 10:00:00', 230.00),
(34, 6, '2026-04-09 14:00:00', 200.00),
(35, 7, '2026-04-10 15:00:00', 240.00),
(36, 8, '2026-04-13 08:30:00', 260.00),
(37, 9, '2026-04-14 09:30:00', 210.00),
(38, 10, '2026-04-15 10:30:00', 150.00),
(39, 11, '2026-04-16 13:30:00', 180.00),
(40, 12, '2026-04-17 15:30:00', 280.00),
(1, 13, '2026-05-04 08:15:00', 230.00),
(2, 14, '2026-05-05 09:30:00', 240.00),
(3, 1, '2026-05-06 10:45:00', 250.00),
(4, 2, '2026-05-07 14:15:00', 180.00),
(5, 3, '2026-05-08 15:45:00', 220.00),
(6, 4, '2026-05-11 08:30:00', 280.00),
(7, 5, '2026-05-12 09:45:00', 230.00),
(8, 6, '2026-05-13 11:00:00', 200.00),
(9, 7, '2026-05-14 13:15:00', 240.00),
(10, 8, '2026-05-15 16:00:00', 260.00),

-- Consultas sem prescricao propositalmente
(11, 9, '2026-06-01 08:00:00', 210.00),
(12, 10,'2026-06-02 09:00:00', 150.00),
(13, 11,'2026-06-03 10:00:00', 180.00),
(14, 12,'2026-06-04 14:00:00', 280.00),
(15, 13, '2026-06-05 15:00:00', 230.00),
(16, 14, '2026-07-06 08:30:00', 240.00),
(17, 1, '2026-07-07 09:30:00', 250.00),
(18, 2, '2026-08-03 10:30:00', 180.00),
(19, 3, '2026-08-17 14:30:00', 220.00),

-- Caso de contorno permitido pela RN17: valor igual a zero
(20, 10,'2026-09-10 15:30:00', 0.00);

-- PRESCRICOES

INSERT INTO Prescricao
(id_consulta, id_medicamento, dosagem, frequencia)
VALUES

-- CONSULTA 1
(1, 6,'50 mg', '1 vez ao dia'),
(1, 12, '10 mg', '1 vez ao dia'),

-- CONSULTA 2
(2, 2, '500 mg', 'A cada 8 horas'),
(2, 4, '500 mg', 'A cada 8 horas'),

-- CONSULTA 3
(3, 9, '10 mg', '1 vez ao dia'),
(3, 19, '10 mg', '1 vez ao dia'),

-- CONSULTA 4
(4, 2, '500 mg', 'A cada 8 horas'),
(4, 3, '400 mg', 'A cada 12 horas'),

-- CONSULTA 5
(5, 3, '400 mg', 'A cada 12 horas'),
(5, 20, '50 mg', 'A cada 12 horas'),

-- CONSULTA 6
(6, 14, '20 mg', '1 vez ao dia'),
(6, 15, '50 mg', '1 vez ao dia'),

-- CONSULTA 7
(7, 2, '500 mg', 'A cada 8 horas'),
(7, 1, '500 mg', 'A cada 6 horas'),

-- CONSULTA 8
(8, 7, '500 mg', '2 vezes ao dia'),
(8, 8, '20 mg', '1 vez ao dia'),

-- CONSULTA 9
(9, 9, '10 mg', '1 vez ao dia'),
(9, 19, '10 mg', '1 vez ao dia'),

-- CONSULTA 10
(10, 1, '500 mg', 'A cada 6 horas'),
(10, 2, '500 mg', 'A cada 8 horas'),

-- CONSULTA 11
(11, 2, '500 mg', 'A cada 8 horas'),
(11, 5, '500 mg', '1 vez ao dia'),

-- CONSULTA 12
(12, 1, '500 mg', 'A cada 6 horas'),
(12, 4, '500 mg', 'A cada 8 horas'),

-- CONSULTA 13
(13, 9, '10 mg', '1 vez ao dia'),
(13, 10, '20 mg', '1 vez ao dia'),

-- CONSULTA 14
(14, 2, '500 mg', 'A cada 8 horas'),
(14, 20, '50 mg', 'A cada 12 horas'),

-- CONSULTA 15
(15, 6, '50 mg', '1 vez ao dia'),
(15, 13, '50 mg', '1 vez ao dia'),

-- CONSULTA 16
(16, 1, '500 mg', 'A cada 6 horas'),
(16, 5, '500 mg', '1 vez ao dia'),

-- CONSULTA 17
(17, 9, '10 mg', '1 vez ao dia'),
(17, 19, '10 mg', '1 vez ao dia'),

-- CONSULTA 18
(18, 2, '500 mg', 'A cada 8 horas'),
(18, 3, '400 mg', 'A cada 12 horas'),

-- CONSULTA 19
(19, 3, '400 mg', 'A cada 12 horas'),
(19, 20, '50 mg', 'A cada 12 horas'),

-- CONSULTA 20
(20, 14, '20 mg', '1 vez ao dia'),
(20, 16, '0,5 mg', 'A noite'),

-- CONSULTA 21
(21, 1, '500 mg', 'A cada 6 horas'),
(21, 2, '500 mg', 'A cada 8 horas'),

-- CONSULTA 22
(22, 7, '500 mg', '2 vezes ao dia'),
(22, 8, '20 mg', '1 vez ao dia'),

-- CONSULTA 23
(23, 9, '10 mg', '1 vez ao dia'),
(23, 19, '10 mg', '1 vez ao dia'),

-- CONSULTA 24
(24, 1, '500 mg', 'A cada 6 horas'),
(24, 8, '20 mg', '1 vez ao dia'),

-- CONSULTA 25
(25, 2, '500 mg', 'A cada 8 horas'),
(25, 4, '500 mg', 'A cada 8 horas'),

-- CONSULTA 26
(26, 1, '500 mg', 'A cada 6 horas'),
(26, 3, '400 mg', 'A cada 12 horas'),

-- CONSULTA 27
(27, 9, '10 mg', '1 vez ao dia'),
(27, 10, '20 mg', '1 vez ao dia'),

-- CONSULTA 28
(28, 2, '500 mg', 'A cada 8 horas'),
(28, 20, '50 mg', 'A cada 12 horas'),

-- CONSULTA 29
(29, 6, '50 mg', '1 vez ao dia'),
(29, 11, '20 mg', 'A noite'),

-- CONSULTA 30
(30, 1, '500 mg', 'A cada 6 horas'),
(30, 5, '500 mg', '1 vez ao dia'),

-- CONSULTA 31
(31, 9, '10 mg', '1 vez ao dia'),
(31, 19, '10 mg', '1 vez ao dia'),

-- CONSULTA 32
(32, 2, '500 mg', 'A cada 8 horas'),
(32, 3, '400 mg', 'A cada 12 horas'),

-- CONSULTA 33
(33, 3, '400 mg', 'A cada 12 horas'),
(33, 20, '50 mg', 'A cada 12 horas'),

-- CONSULTA 34
(34, 14, '20 mg', '1 vez ao dia'),
(34, 15, '50 mg', '1 vez ao dia'),

-- CONSULTA 35
(35, 1, '500 mg', 'A cada 6 horas'),
(35, 2, '500 mg', 'A cada 8 horas'),

-- CONSULTA 36
(36, 7, '500 mg', '2 vezes ao dia'),
(36, 8, '20 mg', '1 vez ao dia'),

-- CONSULTA 37
(37, 9, '10 mg', '1 vez ao dia'),
(37, 19, '10 mg', '1 vez ao dia'),

-- CONSULTA 38
(38, 1, '500 mg', 'A cada 6 horas'),
(38, 8, '20 mg', '1 vez ao dia'),

-- CONSULTA 39
(39, 2, '500 mg', 'A cada 8 horas'),
(39, 5, '500 mg', '1 vez ao dia'),

-- CONSULTA 40
(40, 1, '500 mg', 'A cada 6 horas'),
(40, 4, '500 mg', 'A cada 8 horas'),

-- CONSULTA 41
(41, 3, '400 mg', 'A cada 12 horas'),
(41, 20, '50 mg', 'A cada 12 horas'),

-- CONSULTA 42
(42, 2, '500 mg', 'A cada 8 horas'),
(42, 1, '500 mg', 'A cada 6 horas'),

-- CONSULTA 43
(43, 6, '50 mg', '1 vez ao dia'),
(43, 12, '10 mg', '1 vez ao dia'),

-- CONSULTA 44
(44, 1, '500 mg', 'A cada 6 horas'),
(44, 5, '500 mg', '1 vez ao dia'),

-- CONSULTA 45
(45, 9, '10 mg', '1 vez ao dia'),
(45, 10, '20 mg', '1 vez ao dia'),

-- CONSULTA 46
(46, 2, '500 mg', 'A cada 8 horas'),
(46, 3, '400 mg', 'A cada 12 horas'),

-- CONSULTA 47
(47, 3, '400 mg', 'A cada 12 horas'),
(47, 20, '50 mg', 'A cada 12 horas'),

-- CONSULTA 48
(48, 14, '20 mg', '1 vez ao dia'),
(48, 16, '0,5 mg', 'A noite'),

-- CONSULTA 49
(49, 2, '500 mg', 'A cada 8 horas'),
(49, 4, '500 mg', 'A cada 8 horas'),

-- CONSULTA 50
(50, 7, '500 mg', '2 vezes ao dia'),
(50, 8, '20 mg', '1 vez ao dia');
