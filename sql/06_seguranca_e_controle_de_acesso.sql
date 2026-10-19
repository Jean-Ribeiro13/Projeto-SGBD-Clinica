

USE clinica_medica;

DROP PROCEDURE IF EXISTS sp_criar_usuario;
DROP VIEW IF EXISTS vw_paciente_recepcao;
DROP TABLE IF EXISTS Usuario_App;
DROP ROLE IF EXISTS 'role_recepcao'@'%';
DROP ROLE IF EXISTS 'role_medico'@'%';
DROP ROLE IF EXISTS 'role_admin_clinica'@'%';

CREATE ROLE 'role_recepcao'@'%';
CREATE ROLE 'role_medico'@'%';
CREATE ROLE 'role_admin_clinica'@'%';

GRANT SELECT ON clinica_medica.Paciente TO 'role_recepcao'@'%';
GRANT SELECT, INSERT ON clinica_medica.Consulta TO 'role_recepcao'@'%';
GRANT SELECT ON clinica_medica.Contato_Emergencia TO 'role_recepcao'@'%';
REVOKE SELECT ON clinica_medica.Paciente FROM 'role_recepcao'@'%';

GRANT SELECT ON clinica_medica.Paciente TO 'role_medico'@'%';
GRANT SELECT ON clinica_medica.Consulta TO 'role_medico'@'%';
GRANT SELECT ON clinica_medica.Prontuario TO 'role_medico'@'%';
GRANT SELECT, INSERT, UPDATE ON clinica_medica.Evolucao_Clinica TO 'role_medico'@'%';
GRANT SELECT, INSERT ON clinica_medica.Prescricao TO 'role_medico'@'%';

GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Paciente TO 'role_admin_clinica'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Profissional_Saude TO 'role_admin_clinica'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Medico TO 'role_admin_clinica'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Enfermeiro TO 'role_admin_clinica'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Consulta TO 'role_admin_clinica'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Contato_Emergencia TO 'role_admin_clinica'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Medicamento TO 'role_admin_clinica'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Especialidade TO 'role_admin_clinica'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON clinica_medica.Medico_Especialidade TO 'role_admin_clinica'@'%';
GRANT SELECT ON clinica_medica.Prontuario TO 'role_admin_clinica'@'%';
GRANT SELECT ON clinica_medica.Evolucao_Clinica TO 'role_admin_clinica'@'%';
GRANT SELECT ON clinica_medica.Prescricao TO 'role_admin_clinica'@'%';

CREATE SQL SECURITY DEFINER VIEW vw_paciente_recepcao AS
SELECT id_paciente,
       nome,
       CONCAT('***.***.***-', RIGHT(cpf, 2)) AS cpf_mascarado,
       data_nascimento
FROM Paciente;
GRANT SELECT ON clinica_medica.vw_paciente_recepcao TO 'role_recepcao'@'%';

CREATE TABLE Usuario_App (
    id_usuario INT AUTO_INCREMENT,
    login VARCHAR(50) NOT NULL,
    senha_hash CHAR(64) NOT NULL,
    salt CHAR(32) NOT NULL,
    perfil ENUM('recepcao','medico','admin') NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_usuario_app PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_app_login UNIQUE (login)
);

DELIMITER $$
CREATE PROCEDURE sp_criar_usuario(
    IN p_login VARCHAR(50),
    IN p_senha VARCHAR(100),
    IN p_perfil VARCHAR(20)
)
BEGIN
    DECLARE v_salt CHAR(32);
    SET v_salt = SUBSTRING(SHA2(CONCAT(UUID(), RAND()), 256), 1, 32);
    INSERT INTO Usuario_App (login, senha_hash, salt, perfil)
    VALUES (p_login, SHA2(CONCAT(p_senha, v_salt), 256), v_salt, p_perfil);
END$$
DELIMITER ;
