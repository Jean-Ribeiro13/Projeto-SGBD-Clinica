USE clinica_medica;

-- Visão com uma linha por médico e consulta. A aplicação filtra as datas e
-- agrega os resultados por médico sem duplicar consultas por especialidade.
CREATE OR REPLACE VIEW vw_relatorio_consolidado AS
SELECT
    m.id_profissional AS id_medico,
    prof.nome AS nome_medico,
    m.crm,
    (
        SELECT GROUP_CONCAT(DISTINCT e.nome ORDER BY e.nome SEPARATOR ', ')
        FROM Medico_Especialidade me
        JOIN Especialidade e ON e.id_especialidade = me.id_especialidade
        WHERE me.id_medico = m.id_profissional
    ) AS especialidades,
    c.id_consulta,
    c.id_paciente,
    c.data_hora,
    c.valor AS valor_consulta,
    CASE
        WHEN c.id_consulta IS NULL THEN 0
        ELSE (
            SELECT COUNT(*)
            FROM Prescricao pr
            WHERE pr.id_consulta = c.id_consulta
        )
    END AS quantidade_prescricoes
FROM Medico m
JOIN Profissional_Saude prof ON prof.id_profissional = m.id_profissional
LEFT JOIN Consulta c ON c.id_medico = m.id_profissional;
