USE clinica_medica;

DELIMITER $$

CREATE TRIGGER trg_medico_exclusivo_insert
BEFORE INSERT ON Medico
FOR EACH ROW
BEGIN
    DECLARE v_profissional INT DEFAULT NULL;
    DECLARE v_enfermeiro INT DEFAULT NULL;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET v_enfermeiro = NULL;

    SELECT id_profissional
    INTO v_profissional
    FROM Profissional_Saude
    WHERE id_profissional = NEW.id_profissional
    FOR UPDATE;

    IF v_profissional IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Profissional de saude inexistente.';
    END IF;

    SELECT id_profissional
    INTO v_enfermeiro
    FROM Enfermeiro
    WHERE id_profissional = NEW.id_profissional
    FOR UPDATE;

    IF v_enfermeiro IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT =
                'Este profissional ja esta cadastrado como enfermeiro.';
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER trg_enfermeiro_exclusivo_insert
BEFORE INSERT ON Enfermeiro
FOR EACH ROW
BEGIN
    DECLARE v_profissional INT DEFAULT NULL;
    DECLARE v_medico INT DEFAULT NULL;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET v_medico = NULL;

    SELECT id_profissional
    INTO v_profissional
    FROM Profissional_Saude
    WHERE id_profissional = NEW.id_profissional
    FOR UPDATE;

    IF v_profissional IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Profissional de saude inexistente.';
    END IF;

    SELECT id_profissional
    INTO v_medico
    FROM Medico
    WHERE id_profissional = NEW.id_profissional
    FOR UPDATE;

    IF v_medico IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT =
                'Este profissional ja esta cadastrado como medico.';
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER trg_medico_id_imutavel_update
BEFORE UPDATE ON Medico
FOR EACH ROW
BEGIN
    IF NOT (NEW.id_profissional <=> OLD.id_profissional) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT =
                'Nao e permitido alterar o ID do medico.';
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER trg_enfermeiro_id_imutavel_update
BEFORE UPDATE ON Enfermeiro
FOR EACH ROW
BEGIN
    IF NOT (NEW.id_profissional <=> OLD.id_profissional) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT =
                'Nao e permitido alterar o ID do enfermeiro.';
    END IF;
END$$

DELIMITER ;


