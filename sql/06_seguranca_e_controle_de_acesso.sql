-- =============================================================================================================================
-- B5 — SEGURANÇA E CONTROLE DE ACESSO
-- =============================================================================================================================

USE clinica_medica;

-- -----------------------------------------------------------------------------------------------------------------------------
-- Limpeza prévia: Apaga procedure, views e usuários caso existam, para rodar de maneira limpa
-- -----------------------------------------------------------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_criar_usuario;
DROP VIEW IF EXISTS vw_paciente_recepcao;
DROP TABLE IF EXISTS Usuario_App;
DROP USER IF EXISTS 'recepcao'@'localhost';
DROP USER IF EXISTS 'medico_app'@'localhost';
DROP USER IF EXISTS 'admin_clinica'@'localhost';

-- -----------------------------------------------------------------------------------------------------------------------------
-- Criação de pelo menos três perfis de acesso distintos, com GRANT e REVOKE, aplicando o princípio do menor privilégio.
-- -----------------------------------------------------------------------------------------------------------------------------

-- PERFIL 1: RECEPÇÃO
CREATE USER 'recepcao'@'localhost' IDENTIFIED BY 'R3c3p!2026#Clin'; -- Cria perfil da recepção e sua senha
GRANT SELECT ON clinica_medica.Paciente TO 'recepcao'@'localhost'; -- Concede permissão para visualizar a tabela Paciente
GRANT SELECT ON clinica_medica.Consulta TO 'recepcao'@'localhost'; -- Concede permissão para visualizar a tabela Consulta
GRANT SELECT ON clinica_medica.Contato_Emergencia TO 'recepcao'@'localhost'; -- Concede permissão para visualizar a tabela Contato_Emergencia
GRANT INSERT ON clinica_medica.Consulta TO 'recepcao'@'localhost'; -- Concede permissão para inserir consultas

-- PERFIL 2: MÉDICO
CREATE USER 'medico_app'@'localhost' IDENTIFIED BY 'M3d!c0_2026#App'; -- Cria perfil do médico e sua senha (app se refere a aplicação, foi utilizado com o intuito de distinguir o perfil da tabela Medico)
GRANT SELECT ON clinica_medica.Paciente TO 'medico_app'@'localhost'; -- Concede permissão para visualizar a tabela Paciente
GRANT SELECT ON clinica_medica.Consulta TO 'medico_app'@'localhost'; -- Concede permissão para visualizar a tabela Consulta
GRANT SELECT ON clinica_medica.Prontuario TO 'medico_app'@'localhost'; -- Concede permissão para visualizar a tabela Prontuario
GRANT SELECT ON clinica_medica.Evolucao_Clinica TO 'medico_app'@'localhost'; -- Concede permissão para visualizar a tabela Evolucao_Clinica
GRANT SELECT ON clinica_medica.Prescricao TO 'medico_app'@'localhost'; -- Concede permissão para visualizar a tabela Prescricao
GRANT INSERT, UPDATE ON clinica_medica.Evolucao_Clinica TO 'medico_app'@'localhost'; -- Concede permissão para inserir e alterar evolução
GRANT INSERT ON clinica_medica.Prescricao TO 'medico_app'@'localhost'; -- Concede permissão para inserir prescrições

-- PERFIL 3: ADMINISTRADOR
CREATE USER 'admin_clinica'@'localhost' IDENTIFIED BY 'Adm!n_2026#Clin'; -- Cria o perfil do admnistrador e sua senha
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Paciente TO 'admin_clinica'@'localhost'; -- Concede acesso total a Paciente
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Profissional_Saude TO 'admin_clinica'@'localhost'; -- Concede acesso total a Profissional_Saude
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Medico TO 'admin_clinica'@'localhost'; -- Concede acesso total a Medico
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Enfermeiro TO 'admin_clinica'@'localhost'; -- Concede acesso total a Enfermeiro
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Consulta TO 'admin_clinica'@'localhost'; -- Concede acesso total a Consulta
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Contato_Emergencia TO 'admin_clinica'@'localhost'; -- Concede acesso total a Contato_Emergencia
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Medicamento TO 'admin_clinica'@'localhost'; -- Concede acesso total a Medicamento
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Especialidade TO 'admin_clinica'@'localhost'; -- Concede acesso total a Especialidade
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Medico_Especialidade TO 'admin_clinica'@'localhost'; -- Concede acesso total a Medico_Especialidade
GRANT SELECT ON clinica_medica.Prontuario TO 'admin_clinica'@'localhost'; -- Concede somente leitura em Prontuario
GRANT SELECT ON clinica_medica.Evolucao_Clinica TO 'admin_clinica'@'localhost'; -- Concede somente leitura em Evolucao_Clinica
GRANT SELECT ON clinica_medica.Prescricao TO 'admin_clinica'@'localhost'; -- Concede somente leitura em Prescricao

-- REVOKE explícito 
REVOKE SELECT ON clinica_medica.Paciente FROM 'recepcao'@'localhost';  -- Tira o acesso direto da recepção a Paciente (força usar a visão)
REVOKE INSERT ON clinica_medica.Consulta FROM 'medico_app'@'localhost'; -- Tira do médico o direito de agendar consulta


-- -----------------------------------------------------------------------------------------------------------------------------
--  Ao menos uma visão criada para restringir colunas ou linhas visíveis a determinado perfil.
-- -----------------------------------------------------------------------------------------------------------------------------

CREATE SQL SECURITY DEFINER -- Cria a visão rodando com privilégio do criador (root)
    VIEW vw_paciente_recepcao AS  -- Nome da visão
SELECT
    id_paciente, -- Mostra o id do paciente
    nome, -- Mostra o nome do paciente
    CONCAT('***.***.***-', RIGHT(cpf, 2)) AS cpf_mascarado,  -- Mostra só os 2 últimos dígitos do CPF, resto mascarado
    data_nascimento -- Mostra a data de nascimento
FROM Paciente; -- A visão é baseada na tabela Paciente

GRANT SELECT ON clinica_medica.vw_paciente_recepcao TO 'recepcao'@'localhost';  -- Permite à recepção consultar a visão

-- -----------------------------------------------------------------------------------------------------------------------------
-- Senhas de usuários da aplicação armazenadas com função de hash e sal — nunca em texto claro
-- -----------------------------------------------------------------------------------------------------------------------------

CREATE TABLE Usuario_App ( -- Cria a tabela de usuários da aplicação
    id_usuario INT AUTO_INCREMENT, -- Id numérico que cresce com auto incremento
    login VARCHAR(50)  NOT NULL, -- Nome de login, obrigatório
    senha_hash CHAR(64) NOT NULL,  -- Hash da senha (64 caracteres), obrigatório
    salt CHAR(32) NOT NULL, -- Sal do hash (32 caracteres), obrigatório
    perfil ENUM('recepcao','medico','admin') NOT NULL, -- Perfil do usuário, só aceita esses 3 valores
    ativo BOOLEAN NOT NULL DEFAULT TRUE, -- Se o usuário está ativo, padrão verdadeiro
    CONSTRAINT pk_usuario_app PRIMARY KEY (id_usuario), -- Define id_usuario como chave primária
    CONSTRAINT uq_usuario_app_login UNIQUE (login) -- Impede login repetido
);

DELIMITER $$  -- Troca o delimitador para $$ (para o corpo da procedure)
CREATE PROCEDURE sp_criar_usuario( -- Cria a procedure que cadastra usuário
    IN p_login VARCHAR(50), -- Recebe o login
    IN p_senha VARCHAR(100), -- Recebe a senha em texto (só para gerar o hash)
    IN p_perfil VARCHAR(20) -- Recebe o perfil
)
BEGIN -- Início do corpo da procedure
    DECLARE v_salt CHAR(32); -- Declara a variável que vai guardar o sal
    SET v_salt = SUBSTRING(SHA2(CONCAT(UUID(), RAND()), 256), 1, 32); -- Gera um sal aleatório de 32 caracteres
    INSERT INTO Usuario_App (login, senha_hash, salt, perfil) -- Insere o novo usuário
    VALUES (p_login, SHA2(CONCAT(p_senha, v_salt), 256), v_salt, p_perfil); -- Grava login, hash da senha + sal, sal e perfil
END$$ -- Fim do corpo da procedure
DELIMITER ; -- Volta o delimitador para ;

-- Carga inicial de três usuários de exemplo (um por perfil).
CALL sp_criar_usuario('ana.recepcao', 'SenhaAna2026!', 'recepcao'); -- Cria usuário de exemplo da recepção
CALL sp_criar_usuario('dr.carlos', 'SenhaCarlos2026!', 'medico'); -- Cria usuário de exemplo do médico
CALL sp_criar_usuario('admin.master', 'SenhaAdmin2026!', 'admin'); -- Cria usuário de exemplo do admin



