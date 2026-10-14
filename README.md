# Banco de Dados para Clínica Médica

Projeto SQL para criação e preenchimento do banco `clinica_medica`, com consultas para explorar os dados de pacientes, profissionais de saúde, consultas, prontuários e prescrições.

## Arquivos

| Arquivo | Função |
| --- | --- |
| `01_ddl.sql` | Cria o banco, as 12 tabelas e suas restrições de integridade. |
| `02_dml.sql` | Insere os dados de exemplo e os relacionamentos entre os registros. |
| `03_consultas.sql` | Executa 15 consultas de leitura, com filtros, junções, agregações e subconsultas. |

## Dados e relacionamentos implementados

- **Pacientes:** nome, CPF obrigatório e único e data de nascimento opcional.
- **Contatos de emergência:** nome, telefone e parentesco, vinculados a um paciente.
- **Profissionais de saúde:** nome, CPF e data de nascimento; médicos com CRM e enfermeiros com COREN, ambos obrigatórios e únicos em suas respectivas tabelas.
- **Supervisão médica:** vínculo opcional entre um médico e outro médico supervisor.
- **Especialidades:** associação de médicos a uma ou mais especialidades, com a data de obtenção de cada uma.
- **Consultas:** paciente, médico, data e hora e valor. O banco impede dois registros para o mesmo médico no mesmo instante e valores negativos.
- **Prontuários:** no máximo um por paciente, com tipo sanguíneo opcional restrito aos oito tipos previstos no modelo.
- **Evoluções clínicas:** descrição, data e hora e profissional responsável, vinculadas ao prontuário.
- **Medicamentos e prescrições:** cadastro de medicamentos pelo nome e associação às consultas com dosagem e frequência.

## Como executar

### Requisitos

- Servidor MySQL 8.0.16 ou superior, para aplicar as restrições `CHECK` usadas no DDL.
- Cliente de linha de comando `mysql` ou MySQL Workbench.
- Usuário do banco com permissão para criar o banco e as tabelas, inserir dados e executar consultas.

Execute os arquivos na ordem **01 → 02 → 03**, em uma instalação na qual o banco `clinica_medica` ainda não exista. Todos os scripts já selecionam esse banco com `USE clinica_medica`.

### Pelo terminal

Abra o terminal na pasta que contém os três arquivos SQL e conecte-se ao servidor:

```sh
mysql --default-character-set=utf8mb4 -u SEU_USUARIO -p
```

Substitua `SEU_USUARIO` pelo usuário do MySQL e informe a senha quando solicitada. Dentro do cliente MySQL, execute uma linha de cada vez, verificando se houve erros antes de continuar:

```sql
SOURCE 01_ddl.sql;
SOURCE 02_dml.sql;
SOURCE 03_consultas.sql;
```

Os comandos `SOURCE` devem ser digitados no prompt do MySQL (`mysql>`).

### Pelo MySQL Workbench

1. Abra uma conexão com o servidor MySQL.
2. Abra `01_ddl.sql` e execute o arquivo inteiro para criar a estrutura.
3. Após a execução sem erros, abra e execute todo o arquivo `02_dml.sql`.
4. Abra `03_consultas.sql` e execute o arquivo inteiro ou selecione uma consulta por vez. Se executar apenas um trecho, execute antes `USE clinica_medica;`.
5. Confira os resultados nas grades de saída do Workbench.

### Conferência da carga

Após executar o DDL e o DML uma vez, confira:

```sql
USE clinica_medica;

SELECT COUNT(*) AS total_pacientes FROM Paciente;       -- 40
SELECT COUNT(*) AS total_medicos FROM Medico;           -- 14
SELECT COUNT(*) AS total_consultas FROM Consulta;       -- 60
SELECT COUNT(*) AS total_prontuarios FROM Prontuario;    -- 36
SELECT COUNT(*) AS total_prescricoes FROM Prescricao;   -- 100
```

Os scripts de criação e carga não foram preparados para reexecução sobre uma base já preenchida: o DDL não usa `IF NOT EXISTS`, e o DML depende dos IDs gerados na carga inicial. Repeti-los pode causar erros de duplicidade ou inserir registros adicionais. Para consultar novamente os dados, basta executar `03_consultas.sql`.

## Consultas disponíveis

O arquivo `03_consultas.sql` permite:

- Listar médicos e seus CRMs em ordem alfabética.
- Filtrar pacientes pelo início do nome ou pela ausência de data de nascimento.
- Filtrar consultas por faixa de valor e medicamentos por uma lista fixa de nomes.
- Listar consultas com os nomes dos pacientes e médicos.
- Identificar pacientes sem prontuário e verificar duplicidade de prontuários.
- Somar os valores das consultas por nome de médico e contar prescrições por nome de medicamento.
- Buscar consultas com valor acima da média do próprio médico.
- Identificar profissionais com evoluções clínicas registradas.
- Verificar consultas do mesmo médico na mesma data e hora.
- Contar médicos supervisionados por nome de supervisor.
- Listar os cinco pacientes com maior soma dos valores das consultas, junto à quantidade de prescrições de cada um.

As verificações de prontuários duplicados e de consultas simultâneas para o mesmo médico devem retornar resultados vazios, pois essas duplicidades são impedidas pelas restrições `UNIQUE`. As somas financeiras usam o valor registrado nas consultas; o modelo não registra pagamentos.

## Integrantes

- Jean Ribeiro
- Wendy Pereira
- Angelo Roberto
- Yasmin Gomes
- Lucas Pereira
