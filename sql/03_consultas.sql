USE clinica_medica;
 
-- =====================================================================================================
-- CATEGORIA: BÁSICAS
-- =====================================================================================================
 
-- 1. Quais médicos estão cadastrados, com seu CRM, em ordem alfabética?
-- Relaciona-se com RN02: "Todo médico deve possuir CRM único e de preenchimento obrigatório"
SELECT prof.nome, m.crm 
FROM Medico m -- Seleciona as colunas nome e CRM da tabela Medico
JOIN Profissional_Saude prof ON prof.id_profissional = m.id_profissional -- Une as duas tabelas pela chave em comum
ORDER BY prof.nome ASC; -- Ordena alfabeticamente
 
 
-- 2. Quais pacientes possuem nome iniciado pela letra "M"?
SELECT nome, cpf
FROM Paciente -- Seleciona nome e CPF da tabela Paciente
WHERE nome LIKE 'M%' -- Filtra somente nomes que começam com M
ORDER BY nome; -- Ordena as linhas em ordem alfabética pela coluna nome


-- 3. Quais consultas tiveram valor entre R$150,00 e R$200,00?
-- Relaciona-se com RN17: "O valor cobrado por uma consulta não pode ser inferior a zero"
SELECT id_consulta, data_hora, valor
FROM Consulta -- Seleciona o identificador da consulta, data/hora e valor da tabela consulta
WHERE valor BETWEEN 150.00 AND 200.00 -- Filtra valores entre 150 e 200 (inclusive)
ORDER BY data_hora; -- Ordena por data
 
 
-- 4. Pergunta: Quais medicamentos de uso contínuo estão cadastrados na farmácia da clínica?
-- Relaciona-se com RN19: "Todo medicamento cadastrado deve possuir nome obrigatório"
SELECT id_medicamento, nome
FROM Medicamento -- Seleciona identificador e nome da tabela Medicamento
WHERE nome IN ('Losartana', 'Metformina', 'Sinvastatina', 'Enalapril', 'Atenolol'); -- Filtra só os medicamentos cujo nome está na lista informada
 
 
-- 5. Quais pacientes estão com o cadastro incompleto, sem data de nascimento informada?
SELECT nome, cpf
FROM Paciente -- Seleciona nome e CPF da tabela Paciente
WHERE data_nascimento IS NULL; -- Filtra quem está sem data de nascimento cadastrada
 
 
-- =====================================================================================================
-- CATEGORIA: JUNÇÕES E AGREGAÇÃO 
-- =====================================================================================================
 
-- 6. Quais consultas foram realizadas, com o nome do paciente e do médico responsável?
-- Relaciona-se com RN14: "Toda consulta deve estar vinculada a um paciente e a um médico existentes"
SELECT c.id_consulta, pac.nome AS paciente, prof.nome AS medico, m.crm, c.data_hora, c.valor -- Escolhe colunas de 3 tabelas diferentes
FROM Consulta c 
JOIN Paciente pac ON pac.id_paciente = c.id_paciente -- Une Consulta com Paciente pelo id do paciente.
JOIN Medico m ON m.id_profissional = c.id_medico -- Une com Medico pelo id do médico
JOIN Profissional_Saude prof ON prof.id_profissional = m.id_profissional -- Une com Profissional_Saude para buscar o nome do médico
ORDER BY c.data_hora; -- Ordena por data
 
 
-- 7. Quais pacientes cadastrados ainda não possuem prontuário aberto?
-- Relaciona-se com RN09: "Cada paciente pode possuir no máximo um prontuário"
SELECT p.id_paciente, p.nome
FROM Paciente p -- Seleciona identificador e nome dos pacientes
LEFT JOIN Prontuario pr ON pr.id_paciente = p.id_paciente -- Tenta unir cada paciente com seu prontuário (mesmo se não existir)
WHERE pr.id_prontuario IS NULL; -- Filtra quem ficou sem prontuário na junção
 
 
-- 8. Existe algum paciente com mais de um prontuário cadastrado no sistema?
-- Relaciona-se com RN09: "Relação 1:1 entre Paciente e Prontuário"
SELECT id_paciente, COUNT(*) AS qtd_prontuarios
FROM Prontuario -- Conta quantos prontuários existem por paciente
GROUP BY id_paciente -- Agrupa as linhas por paciente antes de contar
HAVING COUNT(*) > 1; -- Mostra só quem tem mais de 1 prontuário (deve vir vazio)
 
 
-- 9. Qual o faturamento total gerado por cada médico?
SELECT prof.nome AS medico, SUM(c.valor) AS faturamento_total
FROM Consulta c
JOIN Medico m ON m.id_profissional = c.id_medico -- Une as 3 tabelas e soma o valor das consultas
JOIN Profissional_Saude prof ON prof.id_profissional = m.id_profissional
GROUP BY prof.nome -- Agrupa por médico antes de somar
ORDER BY faturamento_total DESC; -- Ordena do maior faturamento pro menor
 
 
-- 10. Quais são os medicamentos mais prescritos na clínica?
-- Relaciona-se com RN20: "Um medicamento pode ser prescrito em várias consultas"
SELECT med.nome AS medicamento, COUNT(*) AS vezes_prescrito
FROM Prescricao pr
JOIN Medicamento med ON med.id_medicamento = pr.id_medicamento -- Une Prescricao com Medicamento e conta ocorrências
GROUP BY med.nome -- Agrupa por medicamento
ORDER BY vezes_prescrito DESC; -- Ordena do mais prescrito para o menos 
 
 
-- =====================================================================================================
-- CATEGORIA: AVANÇADAS 
-- =====================================================================================================
 
-- 11. Quais consultas tiveram valor acima da média cobrada pelo próprio médico?
SELECT c.id_consulta, prof.nome AS medico, c.valor
FROM Consulta c
JOIN Medico m ON m.id_profissional = c.id_medico -- Liga a consulta ao médico
JOIN Profissional_Saude prof ON prof.id_profissional = m.id_profissional -- Busca o nome do médico
WHERE c.valor > (
    SELECT AVG(c2.valor) -- Média de valor calculada só para esse médico
    FROM Consulta c2
    WHERE c2.id_medico = c.id_medico
) -- Compara o valor da consulta com a média só das consultas daquele mesmo médico
ORDER BY prof.nome; -- Ordena por médico
 
 
-- 12. Quais profissionais de saúde já assinaram alguma evolução clínica e, por isso, não podem ser excluídos do sistema?
SELECT prof.id_profissional, prof.nome 
FROM Profissional_Saude prof -- Lista profissionais
WHERE EXISTS (
    SELECT 1
    FROM Evolucao_Clinica ec
    WHERE ec.id_profissional = prof.id_profissional
); -- Mantém quem tem pelo menos um registro de evolução clínica assinado
 
 
-- 13. Existe algum médico com duas consultas marcadas na mesma data e horário?
SELECT c.id_consulta, c.id_medico, c.data_hora
FROM Consulta c -- Lista consultas
WHERE EXISTS ( -- EXISTS funciona como um verdadeiro ou falso
    SELECT 1
    FROM Consulta c2
    WHERE c2.id_medico = c.id_medico
      AND c2.data_hora = c.data_hora
      AND c2.id_consulta <> c.id_consulta
); -- Testa se existe outra consulta (id_consulta diferente) do mesmo médico no mesmo horário
 
 
-- 14. Quantos médicos cada supervisor esta responsável por supervisionar?
SELECT sup.nome AS supervisor, COUNT(m.id_profissional) AS qtd_supervisionados
FROM Medico m
JOIN Medico s ON s.id_profissional = m.id_supervisor
JOIN Profissional_Saude sup ON sup.id_profissional = s.id_profissional -- A tabela Medico é unida com ela mesma (self-join) pra ligar cada médico ao seu supervisor
GROUP BY sup.nome -- Agrupa por supervisor
ORDER BY qtd_supervisionados DESC; -- Ordena do que mais supervisiona (em quantidade) para o que menos supervisiona
 
 
-- 15. Quais são os 5 pacientes que mais gastaram em consultas, e quantas prescrições cada um recebeu no total?

-- Trata-se de uma pergunta de negócio não trivial, pois sua resposta não pode ser obtida pela consulta direta de uma única tabela.
-- É necessario agregar o valor total das consultas por paciente, ordena-los de forma decrescente por gasto e, complementarmente,
-- cruzar essa informação com a tabela de Prescricao para quantificar o volume de medicamentos prescritos a cada um. O resultado tem
-- aplicação prática na gestão financeira e assistencial da clínica, permitindo identificar o perfil dos pacientes de maior movimentação,
-- ou seja, esse dado não está armazenado em lugar nenhum, ele é derivado na hora, cruzando duas tabelas com agregação.

SELECT
    pac.nome,
    SUM(c.valor) AS total_gasto,
    (
        SELECT COUNT(*)
        FROM Prescricao pr
        JOIN Consulta c2 ON c2.id_consulta = pr.id_consulta
        WHERE c2.id_paciente = pac.id_paciente
    ) AS total_prescricoes 
FROM Consulta c
JOIN Paciente pac ON pac.id_paciente = c.id_paciente -- Junta Consulta com Paciente e soma o valor gasto por cada um. A subconsulta no meio do SELECT conta, pra cada paciente, quantas prescrições ele recebeu
GROUP BY pac.id_paciente, pac.nome -- Agrupa por paciente
ORDER BY total_gasto DESC -- Ordena do que mais gastou para o que menos
LIMIT 5; -- Somente os 5 primeiros

