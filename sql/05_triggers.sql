USE clinica_medica;

-- =============================================================================================================================
-- TABELA DE AUDITORIA
-- Armazena o log de todas as operações monitoradas pelas triggers do sistema.
-- =============================================================================================================================

CREATE TABLE IF NOT EXISTS Auditoria_Log (
    id_auditoria INT AUTO_INCREMENT, -- Identificador único do log, gerado automaticamente
    tabela_afetada VARCHAR(50) NOT NULL, -- Nome da tabela que sofreu a alteração ('Paciente' ou 'Consulta')
    operacao VARCHAR(10) NOT NULL, -- Tipo de operação executada (ex: 'UPDATE')
    usuario_bd VARCHAR(100) NOT NULL, -- Identifica o usuário do banco que realizou a alteração (capturado via USER())
    data_hora DATETIME NOT NULL, -- Momento exato em que a alteração ocorreu (capturado via NOW())
    dados_antigos TEXT, -- Registro dos dados anteriores à modificação (prefixo OLD)
    dados_novos TEXT, -- Registro dos dados após a modificação (prefixo NEW)

    CONSTRAINT pk_auditoria_log 
        PRIMARY KEY (id_auditoria)
);

-- =============================================================================================================================
-- GATILHO 1: AUDITORIA NA TABELA PACIENTE
-- Justificativa: A tabela Paciente armazena dados cadastrais sensíveis. Qualquer alteração indevida de nome ou CPF 
-- deve ser rigorosamente rastreada por motivos de segurança e integridade das informações pessoais.
-- =============================================================================================================================

DELIMITER //

CREATE TRIGGER trg_auditoria_paciente_update
AFTER UPDATE ON Paciente
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria_Log (tabela_afetada, operacao, usuario_db, data_hora, dados_antigos, dados_novos)
    VALUES (
        'Paciente',
        'UPDATE',
        USER(),
        NOW(),
        CONCAT('ID: ', OLD.id_paciente, ' | Nome Antigo: ', OLD.nome, ' | CPF Antigo: ', OLD.cpf),
        CONCAT('ID: ', NEW.id_paciente, ' | Nome Novo: ', NEW.nome, ' | CPF Novo: ', NEW.cpf)
    );
END//

DELIMITER ;

-- =============================================================================================================================
-- GATILHO 2: AUDITORIA NA TABELA CONSULTA
-- Justificativa: A tabela Consulta envolve agendamentos médicos e movimentação financeira. Auditar atualizações de valores
-- ou datas previne fraudes e garante o rastreamento do histórico de atendimento.
-- =============================================================================================================================

DELIMITER //

CREATE TRIGGER trg_auditoria_consulta_update
AFTER UPDATE ON Consulta
FOR EACH ROW
BEGIN
    INSERT INTO Auditoria_Log (tabela_afetada, operacao, usuario_bd, data_hora, dados_antigos, dados_novos)
    VALUES (
        'Consulta',
        'UPDATE',
        USER(),
        NOW(),
        CONCAT('ID Consulta: ', OLD.id_consulta, ' | Data/Hora Antiga: ', OLD.data_hora, ' | Valor Antigo: ', OLD.valor),
        CONCAT('ID Consulta: ', NEW.id_consulta, ' | Data/Hora Nova: ', NEW.data_hora, ' | Valor Novo: ', NEW.valor)
    );
END//

DELIMITER ;
