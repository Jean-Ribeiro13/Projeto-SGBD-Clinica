# Testes realizados — Etapa 2

Este documento registra os testes realizados de consulta parametrizada, integridade, transação e concorrência. As evidências estão na pasta `evidencias/`.

## B1 — Consulta parametrizada por médico e período

A consulta relaciona `Medico`, `Profissional_Saude` e `Consulta`, com contagem das consultas e soma dos valores. Os filtros permitem escolher um médico ou todos e informar as datas inicial e final. O `LEFT JOIN` mantém médicos sem consultas no período.

| Teste | Resultado observado | Evidência |
| --- | --- | --- |
| Todos os médicos, de 01/01/2026 a 31/12/2026 | A tela apresentou os médicos e seus totais; a conferência com a consulta no Workbench foi confirmada pela equipe | [Todos os médicos](evidencias/resumo-consultas-todos-medicos.png) |
| Juliana Mendes no mesmo período | Retornou somente Juliana, com 4 consultas e valor total de R$ 800,00 | [Filtro por médica](evidencias/resumo-consultas-filtro-juliana.png) |
| Data inicial 20/01/2026 e final 19/01/2026 | Exibiu mensagem de período inválido, sem apresentar resultados | [Validação do período](evidencias/resumo-consultas-periodo-invertido.png) |
| Juliana em 20/01/2026 | Retornou a médica com quantidade zero e valor total 0,00 | [Período sem consultas](evidencias/resumo-consultas-periodo-sem-consultas.png) |

Os valores representam a soma dos valores registrados nas consultas, não pagamentos recebidos.

## B1 — Consulta SQL de prescrições por medicamento

A consulta foi executada no Workbench com filtro de período e medicamento opcional. Ela relaciona `Medicamento`, `Prescricao` e `Consulta`, contando prescrições e pacientes distintos. O período considera a data da consulta associada à prescrição.

Foram testadas duas seleções: todos os medicamentos, com o parâmetro de medicamento igual a `NULL`, e um medicamento específico. A equipe confirmou que as contagens desse medicamento permaneceram iguais nas duas consultas. Este registro se refere ao teste SQL.

## B3 — Operação transacional de edição de médico

### Implementação e sequência dos comandos

O método `atualizar` de `ProfissionalSaudeRepository` utiliza `@Transactional`. A edição altera primeiro os dados pessoais em `Profissional_Saude` e depois os dados de médico em `Medico`, na mesma transação.

```sql
-- Início da transação: gerenciado pelo Spring.

UPDATE Profissional_Saude
SET nome = ?, cpf = ?, data_nascimento = ?
WHERE id_profissional = ?;

UPDATE Medico
SET crm = ?, id_supervisor = ?
WHERE id_profissional = ?;

-- Retorno normal: COMMIT gerenciado pelo Spring.
-- Exceção de duplicidade propagada: ROLLBACK gerenciado pelo Spring.
```

Esse bloco representa a sequência lógica do código. O `JdbcTemplate` envia os valores como parâmetros. Uma violação de unicidade do CRM gera `DuplicateKeyException`; a exceção sai do repository, provoca a reversão da transação e é tratada pelo controller para apresentar uma mensagem compreensível.

### Falha induzida — 18/10/2026

**Estado inicial:**

| ID | Nome | CRM |
| --- | --- | --- |
| 6 | Juliana Mendes | CRM-DF 26843 |
| 10 | Larissa Oliveira | CRM-DF 28314 |

**Procedimento:**

1. Consultar os dois registros no banco.
2. Abrir a edição de Juliana, ID 6.
3. Alterar o nome para `Juliana Teste Rollback` e o CRM para `CRM-DF 28314`, mantendo os demais campos.
4. Salvar e observar a mensagem de duplicidade.
5. Consultar novamente os registros no banco.

Consulta utilizada para conferir o estado final:

```sql
SELECT p.id_profissional, p.nome, m.crm
FROM Profissional_Saude p
JOIN Medico m ON m.id_profissional = p.id_profissional
WHERE p.id_profissional IN (6, 10)
ORDER BY p.id_profissional;
```

**Resultado:** a aplicação rejeitou o CRM duplicado. Juliana permaneceu com nome `Juliana Mendes` e CRM `CRM-DF 26843`; Larissa permaneceu inalterada. A preservação do nome, atualizado pelo primeiro comando, evidencia a reversão dessa alteração quando o segundo comando falhou. Não houve atualização parcial entre as duas tabelas.

![Estado inicial](evidencias/b3-rollback-antes.png)

![Tentativa rejeitada](evidencias/b3-rollback-tentativa-rejeitada.png)

![Estado final após o rollback](evidencias/b3-rollback-depois.png)

### Isolamento observado na aplicação

A equipe executou, pela aplicação, a consulta abaixo dentro de um método temporário com `@Transactional(readOnly = true)`:

```sql
SELECT @@SESSION.transaction_isolation;
```

O resultado informado foi `REPEATABLE-READ`. Os métodos transacionais não declaram outro isolamento explicitamente e utilizam o padrão da conexão.

**Justificativa:** esse nível permite uma visão consistente nas leituras comuns de uma transação, após a criação do snapshot. A atomicidade das alterações entre tabelas depende da transação. A coordenação das inserções incompatíveis de especialização utiliza também bloqueios explícitos com `FOR UPDATE` e verificações dos triggers. O isolamento, sozinho, não garante totalidade ou exclusividade da especialização.

Leituras com `FOR UPDATE` são leituras com bloqueio e não equivalem às leituras comuns do snapshot. O resultado acima corresponde à conexão da aplicação e não identifica o isolamento das conexões do Workbench.

## B3 — Experimento de concorrência de 18/10/2026

**Objetivo:** observar a espera por bloqueio e a rejeição da tentativa de cadastrar como enfermeiro um profissional já cadastrado como médico.

Foram utilizadas duas conexões independentes, identificadas pela equipe como sessão A = 43 e sessão B = 45. O registro utilizado foi Juliana Mendes, ID 6. A existência do trigger `trg_enfermeiro_exclusivo_insert` foi conferida antes do teste.

### Comandos da sessão A

```sql
START TRANSACTION;

SELECT id_profissional, nome
FROM clinica_medica.Profissional_Saude
WHERE id_profissional = 6
FOR UPDATE;

-- Executado após o início da tentativa na sessão B:
ROLLBACK;
```

### Comando da sessão B

```sql
INSERT INTO clinica_medica.Enfermeiro (id_profissional, coren)
VALUES (6, 'COREN-TESTE-B3');
```

### Comportamento observado

| Momento | Evento |
| --- | --- |
| 11:39:29 | Sessão A iniciou a transação e bloqueou o registro 6 com FOR UPDATE |
| 11:39:48 | Sessão B iniciou o INSERT; a captura mostra o comando em execução |
| 11:40:11 | Sessão A executou ROLLBACK e liberou o bloqueio |
| Após a liberação | O INSERT foi rejeitado com erro 1644: “Este profissional ja esta cadastrado como medico.” |
| Consulta final | Juliana permaneceu médica, com CRM-DF 26843 e COREN NULL |

Não foi observado deadlock nem medido um tempo exato de espera. O horário exibido para o INSERT corresponde ao início do comando.

O histórico da sessão B não registra execução de `START TRANSACTION`, portanto o teste não é descrito como duas transações explicitamente abertas. As capturas também não mostram a grade com o isolamento das sessões 43 e 45; não se atribui a elas o isolamento medido na aplicação.

![Sessão A: bloqueio e liberação](evidencias/b3-concorrencia-sessao-a-bloqueio-liberacao.png)

![Sessão B: espera](evidencias/b3-concorrencia-sessao-b-aguardando.png)

![Sessão B: rejeição](evidencias/b3-concorrencia-sessao-b-rejeicao.png)

### Verificação final

```sql
SELECT p.id_profissional, p.nome, m.crm, e.coren
FROM clinica_medica.Profissional_Saude p
LEFT JOIN clinica_medica.Medico m
    ON m.id_profissional = p.id_profissional
LEFT JOIN clinica_medica.Enfermeiro e
    ON e.id_profissional = p.id_profissional
WHERE p.id_profissional = 6;
```

![Resultado final](evidencias/b3-concorrencia-resultado-final.png)

**Interpretação:** o bloqueio do registro base fez a tentativa da sessão B aguardar. Depois da liberação, o trigger rejeitou a especialização incompatível. O experimento não representa duas criações simultâneas de especialização. O ROLLBACK da sessão A encerrou o bloqueio; a demonstração de reversão de alterações multitabela está no teste de edição.

## B3 — Registro do experimento anterior de concorrência

No experimento anterior, foram utilizadas sessões 10 e 74 e o profissional de teste 32, já cadastrado como médico. A equipe informou `REPEATABLE-READ` nas duas sessões e tabelas InnoDB.

A sessão A bloqueou o registro base com `SELECT ... FOR UPDATE`. A tentativa de inserção em `Enfermeiro` na sessão B aguardou. Após o COMMIT da sessão A, a sessão B recebeu a rejeição por especialização incompatível e executou ROLLBACK. A consulta final mostrou somente o cadastro de médico. Não foi observado deadlock.

- [Sessão A — experimento anterior](evidencias/concorrencia-sessao-a-bloqueio.png)
- [Sessão B — experimento anterior](evidencias/concorrencia-sessao-b-rejeicao.png)
- [Resultado final — experimento anterior](evidencias/concorrencia-resultado-final.png)

Esse registro é separado do experimento de 18/10: usa outro profissional, outras conexões e liberação por COMMIT. O isolamento informado para as sessões 10 e 74 não é atribuído às sessões 43 e 45.
