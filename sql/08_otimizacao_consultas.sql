USE clinica_medica;

-- =============================================================================================================================
-- CONSULTA 1 - Médicos cadastrados (com CRM) em ordem alfabética
-- =============================================================================================================================

-- 1A) Plano e tempo de execução ANTES da criação do índice
EXPLAIN
SELECT prof.nome, m.crm
FROM Medico m
JOIN Profissional_Saude prof ON prof.id_profissional = m.id_profissional
ORDER BY prof.nome ASC;

EXPLAIN ANALYZE
SELECT prof.nome, m.crm
FROM Medico m
JOIN Profissional_Saude prof ON prof.id_profissional = m.id_profissional
ORDER BY prof.nome ASC;                         

-- 1B) Criação do índice secundário na coluna de ordenação
CREATE INDEX idx_profissional_nome ON Profissional_Saude (nome);

-- 1C) Plano e tempo de execução DEPOIS da criação do índice
EXPLAIN
SELECT prof.nome, m.crm
FROM Medico m
JOIN Profissional_Saude prof ON prof.id_profissional = m.id_profissional
ORDER BY prof.nome ASC;

EXPLAIN ANALYZE
SELECT prof.nome, m.crm 
FROM Medico m 
JOIN Profissional_Saude prof ON prof.id_profissional = m.id_profissional 
ORDER BY prof.nome ASC;


-- =============================================================================================================================
-- CONSULTA 3 - Consultas com valor entre R$ 150,00 e R$ 200,00
-- =============================================================================================================================

-- 3A) Plano e tempo de execução ANTES da criação do índice
EXPLAIN
SELECT id_consulta, data_hora, valor
FROM Consulta
WHERE valor BETWEEN 150.00 AND 200.00
ORDER BY data_hora;

EXPLAIN ANALYZE
SELECT id_consulta, data_hora, valor
FROM Consulta
WHERE valor BETWEEN 150.00 AND 200.00
ORDER BY data_hora;                              

-- 3B) Criação de índice composto (filtro por faixa + ordenação/cobertura)
CREATE INDEX idx_consulta_valor_data ON Consulta (valor, data_hora);

-- 3C) Plano e tempo de execução DEPOIS da criação do índice
EXPLAIN
SELECT id_consulta, data_hora, valor
FROM Consulta
WHERE valor BETWEEN 150.00 AND 200.00
ORDER BY data_hora;

EXPLAIN ANALYZE
SELECT id_consulta, data_hora, valor
FROM Consulta
WHERE valor BETWEEN 150.00 AND 200.00
ORDER BY data_hora;                              


-- =============================================================================================================================
-- CONSULTA 9 - Faturamento total gerado por cada médico
-- =============================================================================================================================

-- 9A) Plano e tempo de execução ANTES da criação do índice
EXPLAIN
SELECT prof.nome AS medico, SUM(c.valor) AS faturamento_total
FROM Consulta c
JOIN Medico m ON m.id_profissional = c.id_medico
JOIN Profissional_Saude prof ON prof.id_profissional = m.id_profissional
GROUP BY prof.nome
ORDER BY faturamento_total DESC;

EXPLAIN ANALYZE
SELECT prof.nome AS medico, SUM(c.valor) AS faturamento_total
FROM Consulta c
JOIN Medico m ON m.id_profissional = c.id_medico
JOIN Profissional_Saude prof ON prof.id_profissional = m.id_profissional
GROUP BY prof.nome
ORDER BY faturamento_total DESC;                

-- 9B) Criação de índice composto para FK e agregação de valor
CREATE INDEX idx_consulta_medico_valor ON Consulta (id_medico, valor);
ANALYZE TABLE Consulta;

-- 9C) Plano e tempo de execução DEPOIS da criação do índice
EXPLAIN
SELECT prof.nome AS medico, SUM(c.valor) AS faturamento_total
FROM Consulta c
JOIN Medico m ON m.id_profissional = c.id_medico
JOIN Profissional_Saude prof ON prof.id_profissional = m.id_profissional
GROUP BY prof.nome
ORDER BY faturamento_total DESC;

EXPLAIN ANALYZE
SELECT prof.nome AS medico, SUM(c.valor) AS faturamento_total
FROM Consulta c
JOIN Medico m ON m.id_profissional = c.id_medico
JOIN Profissional_Saude prof ON prof.id_profissional = m.id_profissional
GROUP BY prof.nome
ORDER BY faturamento_total DESC;                
