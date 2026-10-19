# Testes realizados — Etapa 2

Este documento reúne os testes, verificações e evidências realizados pela equipe durante a Etapa 2 do projeto da Clínica Médica. As evidências estão na pasta `evidencias/`.

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

## B2 - Camada de Acesso a Dados

## Objetivo

Nesta etapa, foi analisada a camada de acesso ao banco de dados da aplicação.

Foram analisados os seguintes pontos:
    - Utilização de consultas parametrizadas;
    - Tratamento de entradas que poderiam causar injeção de SQL;
    - Proteção contra comandos não autorizados;
    - Armazenamento das credenciais fora do código-fonte;
    - Ausência de senhas reais no repositório.


## Verificação das consultas

Foi feita uma revisão nos repositories para verificar se os dados recebidos dos formulários eram colocados diretamente nas consultas. Durante essa revisão, foram localizadas algumas concatenações entre partes do SQL. Após a análise, verificou-se que elas utilizavam somente trechos fixos definidos no código, como filtros e ordenações. Não foram encontrados valores digitados pelos usuários sendo inseridos diretamente no texto das consultas. Também não foram encontradas chamadas que montassem comandos SQL utilizando métodos considerados inseguros, como createStatement ou formatação de strings para inserir dados do usuário.

### Resultado

A revisão indicou que os valores recebidos da aplicação são enviados separadamente para o banco. Dessa forma, nomes, identificadores, CPFs e outros dados informados nos formulários não alteram a estrutura das consultas.

### Evidência


![Conferência dos repositorios](evidencias/b2-busca-repositories1.png)


![Conferência dos repositories](evidencias/b2-busca-repositories2.png)


![Conferência dos repositories](evidencias/b2-busca-repositories3.png)


## Preparação do teste

Os testes foram realizados no cadastro de especialidades, utilizando o campo de nome. Essa funcionalidade foi escolhida porque permite que o usuário informe um texto que posteriormente é enviado ao banco. Para verificar uma possível tentativa de exclusão de dados, foi utilizada uma tabela de apoio criada somente para o experimento. Dessa maneira, nenhuma tabela principal do sistema foi colocada em risco.

### Evidência


![Preparação do ambiente](evidencias/b2-preparacao-teste.png)


## Teste de alteração da lógica

Entrada utilizada `x' OR '1'='1`

### Objetivo

Verificar se o valor informado conseguiria alterar a lógica da operação ou fazer a aplicação ignorar o conteúdo digitado no campo.

### Resultado

A entrada foi tratada como texto normal e foi salva como parte do nome da especialidade. A aplicação não retornou registros indevidos e a entrada não alterou o comportamento esperado da operação.

### Evidência


![Teste de alteração da lógica](evidencias/b2-teste-logica1.png)


![Teste de alteração da lógica](evidencias/b2-teste-logica2.png)


## Teste de tentativa de exclusão da tabela

Entrada utilizada `'); DROP TABLE Tabela_Teste_Injecao; --`

### Objetivo

Verificar se a entrada conseguiria encerrar a operação original e executar uma segunda ação não autorizada. Nesse caso, o resultado esperado de uma aplicação vulnerável seria a exclusão da tabela de apoio.

### Resultado

A entrada foi salva como texto comum. A tabela de apoio continuou existindo e o registro criado antes do teste permaneceu disponível. Portanto, o conteúdo digitado não foi interpretado como um comando de banco de dados.

### Evidência


![Teste destrutivo](evidencias/b2-teste-destrutivo1.png)

 
![Teste destrutivo](evidencias/b2-teste-destrutivo2.png)


## Teste de valor inválido na rota

Também foi testado um valor textual no lugar do identificador numérico utilizado pela rota de edição de especialidades. Valor testado `http://localhost:8080/especialidades/1%20OR%201=1/editar`.

### Objetivo

Verificar se a aplicação aceitaria um valor que não corresponde ao formato esperado para o identificador.

### Resultado

A aplicação rejeitou a requisição e retornou o erro HTTP 400. Nenhuma alteração foi realizada no banco. O valor foi interrompido antes de ser utilizado em uma operação de acesso aos dados.

### Evidência


![Teste de rota inválida](evidencias/b2-teste-rota-invalida.png)


##  Resumo dos resultados

| Verificação | Resultado |
| --- | --- |
| Entrada tentando alterar a lógica | Tratada como texto comum |  
| Entrada tentando excluir tabela | Tabela de apoio permaneceu intacta |
| Valor inválido na rota | Requisição rejeitada com erro 400 |
| Acesso a registros não autorizados | Não ocorreu |
| Alteração indevida no banco | Não ocorreu |
| Execução de comando adicional | Não ocorreu |

Com base nos testes realizados, a aplicação resistiu às entradas utilizadas no experimento.


## Conferência dos dados após os testes

Após a execução dos testes, foi realizada uma conferência no banco para verificar se os resultados observados na tela correspondiam aos dados realmente armazenados.
Foram conferidos:
    - Os nomes registrados durante os testes;
    - A quantidade de especialidades;
    - A existência da tabela de apoio;
    - A permanência do registro criado antes do experimento;
    - A ausência de alterações indevidas em outras tabelas.
A quantidade de especialidades após os testes foi de 12 registros. Os valores foram armazenados como texto, sem serem interpretados como comandos. A tabela de apoio permaneceu disponível durante a verificação.

### Evidência

![Conferência dos dados](evidencias/b2-conferencia-banco1.png)


![Conferência dos dados](evidencias/b2-conferencia-banco2.png)


![Conferência dos dados](evidencias/b2-conferencia-banco3.png)


## Limpeza do ambiente de teste

Depois que as verificações foram concluídas, os registros criados durante o experimento foram removidos. A tabela utilizada como apoio também foi excluída. Assim, o banco voltou ao estado anterior ao início dos testes.

### Evidência


![Limpeza dos testes](evidencias/b2-limpeza.png)


## Proteção das credenciais

As informações utilizadas para acessar o banco não ficam escritas diretamente no código-fonte. Os valores reais são configurados localmente por cada integrante da equipe. No repositório ficam somente as referências e as instruções de configuração.

### Evidência


![Configuração sem credenciais](evidencias/b2-configuracao-variaveis1.png)


![Configuração sem credenciais](evidencias/b2-configuracao-variaveis2.png)


![Configuração sem credenciais](evidencias/b2-configuracao-variaveis3.png)


## Arquivos fora do versionamento

Também foi verificado o arquivo .gitignore. As configurações locais da IDE e outros arquivos gerados durante a execução do projeto não são enviados para o Git. Essa medida evita que configurações pessoais ou informações sensíveis sejam compartilhadas por engano.

### Evidência

![Arquivo gitignore](evidencias/b2-gitignore.png)

## Busca por senhas no repositório

Foi realizada uma busca no repositório por termos relacionados a senhas, usuários e credenciais. A busca não encontrou senhas reais armazenadas em arquivos versionados. Foram encontradas apenas referências às variáveis de ambiente e instruções para configuração local.

### Evidência

![Busca por credenciais](evidencias/b2-busca-credenciais.png)

## Conclusão da B2

Os testes realizados indicaram que a aplicação utiliza uma forma adequada de acesso ao banco para as operações analisadas. As entradas utilizadas no experimento não conseguiram alterar a lógica das operações, acessar registros indevidos ou executar comandos adicionais. A tentativa de exclusão da tabela de apoio não teve efeito, e o valor inválido informado na rota foi rejeitado pela aplicação. Também foi verificado que as credenciais do banco são configuradas por variáveis de ambiente e não ficam expostas nos arquivos versionados. Portanto, os testes realizados atenderam aos principais objetivos da B2.

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

## B3 - Experimento de concorrência de 18/10/2026

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

## B3 - Registro do experimento anterior de concorrência

No experimento anterior, foram utilizadas sessões 10 e 74 e o profissional de teste 32, já cadastrado como médico. A equipe informou `REPEATABLE-READ` nas duas sessões e tabelas InnoDB.

A sessão A bloqueou o registro base com `SELECT ... FOR UPDATE`. A tentativa de inserção em `Enfermeiro` na sessão B aguardou. Após o COMMIT da sessão A, a sessão B recebeu a rejeição por especialização incompatível e executou ROLLBACK. A consulta final mostrou somente o cadastro de médico. Não foi observado deadlock.

- [Sessão A — experimento anterior](evidencias/concorrencia-sessao-a-bloqueio.png)
- [Sessão B — experimento anterior](evidencias/concorrencia-sessao-b-rejeicao.png)
- [Resultado final — experimento anterior](evidencias/concorrencia-resultado-final.png)

Esse registro é separado do experimento de 18/10: usa outro profissional, outras conexões e liberação por COMMIT. O isolamento informado para as sessões 10 e 74 não é atribuído às sessões 43 e 45.

## B4 — Otimização de Consultas

Este tópico apresenta o estudo de desempenho de três consultas do sistema da clínica, escolhidas entre as quinze do arquivo `03_consultas.sql`. Para cada uma foi seguido o mesmo procedimento:

1. Obter o plano de execução com `EXPLAIN` antes de qualquer intervenção e interpretá-lo em texto.
2. Medir o tempo real de execução com `EXPLAIN ANALYZE`.
3. Criar um índice, justificando a escolha das colunas e a ordem delas.
4. Obter o plano e o tempo depois da intervenção.
5. Comparar os resultados, informando o volume de dados, e discutir com honestidade o que aconteceu, inclusive quando o índice não trouxe ganho.

Como o tempo foi medido: cada `EXPLAIN ANALYZE` foi executado 5 vezes. A primeira execução foi descartada (cache frio) e foi usada a mediana das quatro seguintes. O valor registrado é o tempo até a última linha do primeiro nó do plano (`actual time=a..b`, valor `b`, em milissegundos). Antes de cada medição foi executado ANALYZE TABLE para atualizar as estatísticas do otimizador.

Os estudos foram executados sequencialmente. Antes de cada intervenção, foram registrados os índices existentes. Na Consulta 9, o índice idx_profissional_nome, criado no estudo anterior, ainda estava presente, embora o plano apresentado tenha utilizado a chave primária de Profissional_Saude. Esse índice foi removido ao final do conjunto de experimentos.


## Ambiente e volume de dados

### Comportamento observado


| Item | Valor |
| --- | --- |
| Versão do MySQL | 8.0.46 |  
| Linhas em Consulta | 60 |
| Linhas em Medico | 14 |
| Linhas em Profissional_Saude | 20 |


<img width="557" height="326" alt="image" src="https://github.com/user-attachments/assets/ebc3653e-92f8-48bf-a6e3-2b53b37aa12f" />

<img width="508" height="323" alt="image" src="https://github.com/user-attachments/assets/facc19f0-6235-4e8a-9088-8b1133eec1be" />

<img width="542" height="323" alt="image" src="https://github.com/user-attachments/assets/91893672-095a-467f-aaa9-d493174cbede" />

<img width="527" height="338" alt="image" src="https://github.com/user-attachments/assets/9be1dff5-6b8b-4f1c-b371-474c669e079c" />


Índices que já existiam antes da intervenção (importante para entender o ganho dos índices novos).


<img width="1093" height="218" alt="image" src="https://github.com/user-attachments/assets/deb4bc16-3291-4bcf-a124-6ea5f1f0a1b4" />

<img width="1173" height="204" alt="image" src="https://github.com/user-attachments/assets/9f21cb83-172c-4475-ac4d-2488fb078cdb" />

<img width="1155" height="167" alt="image" src="https://github.com/user-attachments/assets/8e2283c5-f35e-435d-8366-66cdc7049a5c" />


Observação: a tabela `Consulta` já possui a chave primária e a restrição UNIQUE `(id_medico, data_hora)`, criada para a regra RN16, além do índice criado pelo MySQL para a chave estrangeira de paciente.


## Estudo das consultas

Consulta 1: médicos cadastrados em ordem alfabética

Pergunta de negócio: quais médicos estão cadastrados, com seu CRM, em ordem alfabética? (relacionada à RN02).

### Plano antes da intervenção

<img width="1084" height="212" alt="image" src="https://github.com/user-attachments/assets/a0fb78ba-e39b-4b2e-8e6e-18ab9243c4f7" />


| Tabela | type | key | rows | Extra |
| --- | --- | --- | --- | --- | 
| Medico | index | uq_medico_crm | 14 | Using index; Using temporary; Using filesort |
| Profissional_Saude | eq_ref | PRIMARY | 1 | NULL |


<img width="1374" height="282" alt="image" src="https://github.com/user-attachments/assets/4ab6173b-b423-43d2-9807-2a9cc4380cfd" />


Antes da intervenção, o plano percorreu o índice uq_medico_crm de Medico e, para cada médico, buscou o registro correspondente em Profissional_Saude pela chave primária. O resultado passou por uma etapa de ordenação por nome. A mediana do tempo observado no nó raiz do plano foi de 0,0813 ms.


### Índice criado

```sql 
CREATE INDEX idx_profissional_nome ON Profissional_Saude (nome);
```

Justificativa das colunas e da ordem: o índice foi criado sobre nome por ser a coluna utilizada no ORDER BY. Como existe apenas uma coluna explicitamente indexada, não há uma ordem entre múltiplas colunas a justificar. No InnoDB, o índice secundário também contém a chave primária id_profissional, podendo fornecer as colunas dessa tabela necessárias à consulta. O objetivo era possibilitar uma leitura ordenada por nome e reduzir a necessidade de ordenação adicional. Entretanto, esse benefício depende do plano de execução e da ordem de acesso às tabelas. No experimento, o otimizador não utilizou o índice criado e manteve a ordenação.

### Plano depois da intervenção

<img width="1024" height="211" alt="image" src="https://github.com/user-attachments/assets/33239b51-b11e-4be5-aa28-7c9a3d18f239" />



| Tabela | type | key | rows | Extra |
| --- | --- | --- | --- | --- | 
| Medico | index | uq_medico_crm | 14 | Using index; Using temporary; Using filesort |
| Profissional_Saude | eq_ref | PRIMARY | 1 | NULL |


<img width="1386" height="274" alt="image" src="https://github.com/user-attachments/assets/631cd0af-3f24-4a61-9615-ddc8001324ba" />



| Execução | Antes(ms) | Depois(ms) |
| --- | --- | --- |
| 2ª | 0.0793 | 0.0998 |
| 3ª | 0.0859 | 0.109 |
| 4ª | 0.0823 | 0.079 |
| 5ª | 0.0803 | 0.0841 |
| Mediana | 0.0813| 0.09195 |


### Discussão

Após a criação de idx_profissional_nome, o otimizador manteve o plano anterior e não utilizou o novo índice. Permaneceram a leitura de Medico pelo índice uq_medico_crm, a busca em Profissional_Saude pela chave primária e a ordenação por nome. A mediana passou de 0,0813 ms para 0,09195 ms, portanto não houve ganho demonstrado neste experimento. Como o plano permaneceu igual e os tempos são muito pequenos, essa diferença não permite atribuir a piora diretamente ao índice criado. O baixo volume de dados e a ordem de acesso às tabelas são fatores relevantes, mas o uso futuro do índice dependerá de novas avaliações.

Consulta 3: consultas com valor entre R$150,00 e R$200,00

Pergunta de negócio: quais consultas tiveram valor entre R$150,00 e R$200,00?

### Plano antes da intervenção


<img width="776" height="199" alt="image" src="https://github.com/user-attachments/assets/1ba945ec-5cb8-4f34-8166-eb09064edd5a" />



| Tabela | type | key | rows | Extra |
| --- | --- | --- | --- | --- | 
| Consulta | ALL | NULL | 60 | Using where; Using filesort |



<img width="1180" height="308" alt="image" src="https://github.com/user-attachments/assets/86fbd6fc-d253-4c40-8293-9ab619458ba5" />


Não existe índice que comece por valor, então o MySQL lê a tabela Consulta inteira , aplica o filtro BETWEEN linha a linha (Using where) e depois ordena as linhas que passaram por data_hora.

### Índice criado

```sql 
CREATE INDEX idx_consulta_valor_data ON Consulta (valor, data_hora);
```

Justificativa das colunas e da ordem: o filtro é uma faixa (`BETWEEN`) em valor, por isso valor vem primeiro: o índice permite ir direto à faixa de 150 a 200 sem ler o restante. data_hora vem em segundo para que o índice contenha todas as colunas do SELECT (`id_consulta` já vem embutida, por ser a chave primária), tornando a consulta coberta pelo índice, sem acessar a tabela. Como a faixa está em valor, a ordem por data_hora não é aproveitada e a ordenação das linhas filtradas tende a continuar. A ordem inversa, (`data_hora, valor`), evitaria a ordenação, mas obrigaria a percorrer todo o índice; escolhemos valor primeiro porque reduz as linhas lidas.

### Plano depois da intervenção


<img width="964" height="216" alt="image" src="https://github.com/user-attachments/assets/e4f81b4e-eb49-49e0-b270-047942482d18" />



| Tabela | type | key | rows | Extra |
| --- | --- | --- | --- | --- | 
| Consulta | range | idx_consulta_valor_data | 17 | Using where; Using filesort |



<img width="1112" height="258" alt="image" src="https://github.com/user-attachments/assets/67e7fd77-afb6-4e01-a9f2-d0d40291a83c" />



Comparação de tempo (volume da tabela Consulta: 60 registros; resultado do filtro: 17 registros.)


| Execução | Antes(ms) | Depois(ms) |
| --- | --- | --- |
| 2ª | 0.0457 | 0.0178 |
| 3ª | 0.0437 | 0.0256 |
| 4ª | 0.0447 | 0.0278 |
| 5ª | 0.0836 | 0.0475 |
| Mediana | 0.452| 0.217 |


### Discussão 

O otimizador passou a utilizar idx_consulta_valor_data, com o tipo de acesso mudando de ALL para range e a estimativa de linhas examinadas passando de 60 para 17. O plano indica leitura coberta pelo índice, mas mantém a ordenação por data_hora. Os tempos registrados apresentaram mediana de 0,442 ms antes e 0,217 ms depois. Os tempos apresentaram variação entre as execuções, com valores menores na quinta medição de ambos os cenários. Assim, a redução da mediana deve ser interpretada em conjunto com a mudança do plano de execução, sem atribuir toda a diferença exclusivamente ao índice. O experimento não permite identificar a causa dessa oscilação.

Consulta 9: faturamento total por médico

Pergunta de negócio: qual o faturamento total gerado por cada médico?

### Plano antes da intervenção

<img width="1201" height="272" alt="image" src="https://github.com/user-attachments/assets/677a01c4-898a-4cb2-a536-dea6e04a5354" />


| Tabela | type | key | rows | Extra |
| --- | --- | --- | --- | --- | 
| Medico | eq_ref | fk_medico_supervisor | 14 | Using index; Using temporary; Using filesort |
| Profissional_Saude | eq_ref | PRIMARY | 1 | NULL |
| Consulta | ref | uq_medico_data_hora | 4 | NULL |


<img width="586" height="260" alt="image" src="https://github.com/user-attachments/assets/b02bdf1d-7a1a-48a0-bb92-ec89478338e0" />


O plano inicia pela leitura de Medico pelo índice fk_medico_supervisor. Para cada médico, busca o profissional correspondente pela chave primária e acessa suas consultas pelo índice uq_medico_data_hora. Como esse índice não contém valor, é necessário acessar os registros de Consulta para obter a coluna utilizada na soma. O plano também indica tabela temporária e ordenação para produzir o resultado agregado.

### Índice criado

```sql 
CREATE INDEX idx_consulta_medico_valor ON Consulta (id_medico, valor);
```

Justificativa das colunas e da ordem: a junção e o agrupamento são por médico, então `id_medico` vem primeiro: o índice entrega as consultas já agrupadas por médico. valor vem em seguida porque é a coluna somada: com ela no índice, a soma é calculada só pelo índice, sem ler a tabela (`Using index`). A restrição `UNIQUE (id_medico, data_hora`) que já existe também começa por id_medico, mas não contém valor, então ainda exigiria acessar a tabela para cada consulta. Como o `GROUP BY` é por `prof.nome` e a ordenação é pelo total (uma função agregada), esses dois passos independem do índice e podem continuar aparecendo no plano (`Using temporary, Using filesort`); por isso o ganho esperado é limitado à leitura de `Consulta`.

### Plano depois da intervenção


<img width="1302" height="318" alt="image" src="https://github.com/user-attachments/assets/4ac3ab14-7523-4440-88fb-d1e4a440cf2a" />


| Tabela | type | key | rows | Extra |
| --- | --- | --- | --- | --- | 
| Medico | index | fk_medico_supervisor | 14 | Using index; Using temporary; Using filesort |
| Profissional_Saude | eq_ref | PRIMARY | 1 | NULL |
| Consulta | ref | idx_consulta_medico_valor | 4 | Using index |


<img width="1357" height="334" alt="image" src="https://github.com/user-attachments/assets/e69217d1-c8d3-413e-a588-9d4a29290b92" />


Comparação de tempo (volume da tabela Consulta: 60 registros; volume da tabela Medico: 14 registros.)


| Execução | Antes(ms) | Depois(ms) |
| --- | --- | --- |
| 2ª | 0.217 | 0.0276 |
| 3ª | 0.374 | 0.0409 |
| 4ª | 0.218 | 0.0525 |
| 5ª | 0.233 | 0.0277 |
| Mediana | 0.2255| 0.0343 |


### Discussão 

O otimizador passou a usar idx_consulta_medico_valor. O tempo mediano foi de 0,2255 ms para 0,0343 ms . O Using temporary e o Using filesort permaneceram, porque o agrupamento é por nome e a ordenação é pelo total, passos que o índice não resolve. Como melhoria de correção semântica, o agrupamento poderia incluir prof.id_profissional e prof.nome, evitando reunir médicos homônimos. O efeito dessa alteração no desempenho precisaria ser medido em um novo experimento.


## Custo dos índices e decisão final

Índices não são de graça: cada índice adicional ocupa espaço em disco e precisa ser atualizado a cada INSERT, UPDATE ou DELETE na tabela. Em Consulta, que é a tabela de maior movimento do sistema (agendamento contínuo), dois índices novos significam duas estruturas a mais para manter em cada nova consulta. Por isso a decisão de manter ou remover cada índice considerou o benefício medido contra esse custo.

• idx_profissional_nome: Removido após a conclusão dos testes. O otimizador não utilizou o índice na Consulta 1, e não houve ganho demonstrado na carga avaliada. Por esse motivo, optou-se por não manter essa estrutura adicional. Seu eventual uso futuro deverá ser reavaliado com novas medições.

• idx_consulta_valor_data: Mantido. Na consulta avaliada, possibilitou acesso por faixa em valor e leitura coberta pelo índice, mantendo a ordenação por data_hora. Sua manutenção foi considerada adequada para essa consulta, com base na mudança de plano e nos tempos registrados.

• idx_consulta_medico_valor: Mantido. O plano posterior informado utiliza o índice para localizar as consultas por médico e obter valor no próprio índice, reduzindo a necessidade de acessar os registros de Consulta. A manutenção foi considerada adequada para o relatório de faturamento estudado.

## Conclusão

Na carga avaliada, o índice da Consulta 1 não foi escolhido pelo otimizador e não apresentou ganho demonstrado. Na Consulta 3, houve mudança de varredura completa para busca por faixa com índice de cobertura. Na Consulta 9, o índice proposto busca reduzir o acesso aos registros de Consulta ao incluir a coluna valor. As comparações de tempo devem ser interpretadas considerando o pequeno volume da base e foram analisadas em conjunto com os planos de execução e os registros de medição apresentados. Não foram realizados testes de escalabilidade, portanto os resultados não permitem garantir o comportamento em produção.
