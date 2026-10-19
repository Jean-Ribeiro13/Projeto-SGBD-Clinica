# Clínica Médica — Spring Boot e MySQL

Projeto acadêmico de banco de dados com aplicação web em Java, Spring Boot e MySQL. Possui telas de cadastro, listagem, edição e exclusão de pacientes, médicos, enfermeiros, medicamentos e especialidades, além de uma listagem geral de profissionais de saúde.

As demais entidades do banco ainda não possuem telas CRUD. A aplicação inclui resumos parametrizados e um relatório consolidado baseado em uma visão SQL. O modelo não registra pagamentos nem status de realização das consultas.

## Funcionalidades implementadas

### Pacientes

- Listagem em ordem alfabética e cadastro e edição de nome, CPF e data de nascimento.
- Nome obrigatório, com até 100 caracteres; CPF com exatamente 11 dígitos; data de nascimento opcional.
- Tratamento de CPF duplicado, preservando os dados preenchidos.
- Exclusão com confirmação e mensagens de sucesso ou bloqueio.
- Consultas ou prontuário vinculados impedem a exclusão. Quando permitida, os contatos de emergência são removidos em cascata.

### Profissionais de saúde, médicos e enfermeiros

- Cadastro de médico com CRM e supervisor opcional, ou de enfermeiro com COREN.
- Cada cadastro cria um profissional e sua especialização com o mesmo ID, em uma única transação.
- Listagem geral de profissionais, listagens por especialização e edição dos dados.
- Nome, CPF e data de nascimento obrigatórios. CRM e COREN são obrigatórios nos respectivos formulários e únicos em suas tabelas.
- O supervisor deve ser outro médico existente. O próprio médico é excluído das opções ao abrir a edição e ao retornar com erro; a escolha também é validada no servidor.
- Exclusão completa do profissional e de sua especialização, quando não existem vínculos impeditivos. Consultas e evoluções clínicas podem bloquear a operação.
- Mensagens de duplicidade de CPF ou registro profissional e preservação dos campos do formulário.

Não há cadastro de profissional sem especialização nem operação de exclusão apenas da especialização. Também não há fluxo para converter médico em enfermeiro ou vice-versa.

A validação do CPF verifica o formato, sem calcular os dígitos verificadores. Sua unicidade é verificada separadamente nas tabelas de pacientes e profissionais. CRM e COREN têm limite de 20 caracteres; a aplicação ainda não valida um padrão específico de formato desses registros.

### Especialidades

- Cadastro, edição, listagem e exclusão.
- Listagem com a quantidade de médicos vinculados a cada especialidade.
- Validação do nome e tratamento de nomes duplicados, preservando o campo preenchido.
- Bloqueio de exclusão quando existem médicos vinculados.

A contagem da listagem é complementar aos relatórios parametrizados com filtros, junções e agregações.

### Relatório consolidado de consultas

- A rota `/relatorios/consolidado` apresenta uma visão consolidada por médico, com filtro por período e médico.
- O resultado mostra CRM, especialidades, consultas, pacientes distintos, prescrições, valor total registrado e valor médio por consulta.
- A tela consulta a visão `vw_relatorio_consolidado`, criada por `sql/07_vw_relatorio_consolidado.sql`. O valor das consultas não representa pagamentos recebidos.

### Medicamentos

- Listagem, cadastro, edição e exclusão na mesma página.
- Validações no servidor e tratamento de erros estão em revisão por um integrante da equipe; a conclusão desses ajustes ainda precisa ser conferida.

## Integridade e transações

Os métodos de criação e atualização de `ProfissionalSaudeRepository` usam `@Transactional`. Para médicos e enfermeiros, as alterações em `Profissional_Saude` e na tabela da especialização pertencem à mesma transação.

O script `04_integridade_especializacao.sql` instala quatro triggers:

- Duas impedem inserir um profissional como médico se já for enfermeiro, e vice-versa. Utilizam bloqueio do registro base com `FOR UPDATE`.
- Duas impedem alterar diretamente o identificador nas tabelas `Medico` e `Enfermeiro` por `UPDATE`.

A totalidade da especialização depende dos fluxos da aplicação: ela cria o profissional junto com sua especialização e exclui o conjunto. Os triggers não impedem, por si só, que uma operação SQL direta crie um profissional sem especialização ou remova somente a especialização. Os triggers de `UPDATE` também não interceptam atualizações propagadas por cascata de chave estrangeira.

Esses triggers são de integridade, não de auditoria.

### Verificações realizadas e limites

- Na edição de médica, a tentativa de alterar o nome e usar um CRM existente foi rejeitada. Ao reabrir, nome e CRM originais estavam preservados, evidenciando o rollback da edição multitabela.
- Na edição de enfermeiro, um CPF pertencente a outro profissional foi rejeitado e o CPF original permaneceu ao reabrir o formulário.
- O filtro de supervisor foi verificado ao abrir a edição e após erro de CRM duplicado.
- Foram testadas as rejeições de dupla especialização e de alteração direta do ID da especialização.
- No experimento de concorrência, duas sessões InnoDB em `REPEATABLE-READ` foram utilizadas. A sessão A bloqueou com `SELECT ... FOR UPDATE` o registro base de um profissional já cadastrado como médico. A sessão B tentou inseri-lo como enfermeiro: aguardou a liberação do bloqueio e depois recebeu a rejeição do trigger, seguida de `ROLLBACK`. O resultado final manteve somente a especialização de médico.

Esse experimento demonstra espera por bloqueio e posterior rejeição; não corresponde a duas criações simultâneas de especialização. Ainda falta verificar e justificar o nível de isolamento efetivamente utilizado pela conexão da aplicação.

A complementação das evidências do cadastro com CRM duplicado e da exclusão completa de um profissional de teste ficou pendente. O histórico de resultados e as capturas devem ser mantidos em [testes da Etapa 2](docs/testes-etapa2.md) e `docs/evidencias/`. Não há suíte de testes automatizados documentada.

## Tecnologias

- JDK 25 e Spring Boot 4.1.1.
- Maven, Spring Web MVC e Thymeleaf.
- Spring JDBC, Bean Validation e MySQL Connector/J.
- MySQL 8.0.16 ou superior, com tabelas InnoDB.

O projeto utiliza SQL explícito e parâmetros nas operações que recebem dados do usuário. A estrutura do banco é criada pelos scripts SQL.

## Estrutura do repositório

```text
repositorio/
├── README.md
├── .gitignore
├── docs/
│   ├── testes-etapa2.md
│   └── evidencias/
├── sql/
│   ├── 01_ddl.sql
│   ├── 02_carga.sql
│   ├── 03_consultas.sql
│   ├── 04_integridade_especializacao.sql
│   ├── 05_triggers.sql
│   ├── 06_seguranca_e_controle_de_acesso.sql
│   ├── 07_vw_relatorio_consolidado.sql
│   ├── 08_otimizacao_consulta.sql
└── clinica/
    ├── pom.xml
    └── src/main/
        ├── java/br/edu/clinica/
        │   ├── controller/
        │   ├── dto/
        │   ├── model/
        │   ├── repository/
        │   └── ClinicaApplication.java
        └── resources/
            ├── templates/
            ├── application.properties
            ├── application-mysql.properties
            └── application-sem-banco.properties
```

## Como executar

### 1. Preparar o ambiente

Instale o JDK 25, o MySQL Server, o IntelliJ IDEA e o MySQL Workbench ou outro cliente SQL. É possível usar o Maven integrado ao IntelliJ; a primeira abertura precisa de internet para baixar as dependências.

Clone o repositório ou extraia o ZIP antes de abrir o projeto.

### 2. Preparar o banco

Para uma instalação nova, abra os arquivos no Workbench e execute nesta ordem, conferindo os erros entre cada etapa:

1. `sql/01_ddl.sql`: cria o banco `clinica_medica`, as tabelas e as restrições.
2. `sql/02_carga.sql`: insere os dados de exemplo.
3. `sql/04_integridade_especializacao.sql`: instala os quatro triggers de integridade. Execute o arquivo completo, incluindo os comandos `DELIMITER`, com um usuário autorizado a criar triggers.
4. `sql/05_triggers.sql`: cria os objetos relacionados ao registro de alterações.
5. `sql/06_seguranca_e_controle_de_acesso.sql`: cria os perfis, usuários e permissões mínimas do banco.
6. `sql/07_vw_relatorio_consolidado.sql`: cria ou atualiza a visão usada pelo relatório consolidado.
7. Opcionalmente, `sql/03_consultas.sql`: executa consultas de leitura para explorar os dados; não é necessário para iniciar a aplicação.

O DDL e a carga destinam-se a uma base nova. Não repita esses arquivos em um banco preenchido: `CREATE DATABASE IF NOT EXISTS` não torna toda a instalação reexecutável. O script de triggers também usa `CREATE TRIGGER` e não deve ser repetido se os triggers já existem.

Em uma base existente, confira a estrutura, os dados e os triggers antes de aplicar alterações. A criação dos triggers não corrige registros inconsistentes que já estavam no banco.

Para conferir a instalação:

```sql
USE clinica_medica;

SELECT COUNT(*) AS total_pacientes FROM Paciente;
SHOW TRIGGERS FROM clinica_medica;
```

A carga original contém 40 pacientes; esse total muda com o uso da aplicação. Na instalação nova, a segunda consulta deve mostrar os quatro triggers definidos no script 04.

Os scripts reproduzem a estrutura e os dados de exemplo, não as alterações feitas posteriormente pela aplicação.

### 3. Abrir no IntelliJ

1. Escolha **Open** e selecione a pasta `clinica`, que contém `pom.xml`.
2. Importe como projeto Maven, se solicitado, e aguarde as dependências.
3. Em **File > Project Structure > Project**, selecione o JDK 25.
4. Confira se o Maven e a configuração de execução também utilizam o JDK 25.
5. Se necessário, adicione a pasta de instalação do JDK do seu computador.

### 4. Configurar a conexão

Abra `ClinicaApplication.java` e crie a configuração de execução pelo botão junto ao método `main`, caso ela ainda não exista. Em **Run > Edit Configurations**, selecione essa configuração.

Em **Active profiles**, informe `mysql`. Como alternativa, use a variável de ambiente `SPRING_PROFILES_ACTIVE=mysql`.

Em **Environment variables**, configure:

| Variável | Valor |
| --- | --- |
| `DB_USER` | Usuário do MySQL |
| `DB_PASSWORD` | Senha desse usuário |
| `DB_URL` | `jdbc:mysql://localhost:3306/clinica_medica` |

Ajuste host e porta conforme a conexão local. Se o campo não aparecer, procure **Environment variables** em **Modify options**.

Cada integrante configura suas credenciais localmente. Não versione senhas ou configurações de execução contendo credenciais. Mantenha **Store as project file** desmarcado e `.idea/` fora do versionamento.

### 5. Iniciar e acessar

Execute `ClinicaApplication`, aguarde `Started ClinicaApplication` no console e abra [a página inicial](http://localhost:8080).

| Rota | Função |
| --- | --- |
| `/` | Página inicial |
| `/pacientes` | Listagem e acesso às operações de pacientes |
| `/pacientes/novo` | Cadastro de paciente |
| `/profissionais` | Listagem geral, edição dos dados básicos e exclusão completa |
| `/medicos` | Listagem e acesso às operações de médicos |
| `/enfermeiros` | Listagem e acesso às operações de enfermeiros |
| `/medico/novo` | Cadastro de médico e seu profissional base |
| `/enfermeiro/novo` | Cadastro de enfermeiro e seu profissional base |
| `/medicamentos` | Listagem, cadastro, edição e exclusão de medicamentos |
| `/especialidades` | Listagem e acesso às operações de especialidades |
| `/especialidades/novo` | Cadastro de especialidade |
| `/relatorios/consolidado` | Relatório consolidado de consultas por médico e período |
| `/teste-banco` | Teste de conexão com `SELECT 1` |

As rotas de cadastro e edição de médicos e enfermeiros utilizam o singular, enquanto as listagens utilizam o plural. Navegue pelos links da aplicação.

O teste de conexão não comprova a existência das tabelas, da carga ou dos triggers. O perfil padrão `sem-banco` permite abrir a página inicial; para utilizar os CRUDs, ative `mysql`.

## Papel dos arquivos

- `pom.xml`: define a versão do Java, o Spring Boot e as dependências baixadas pelo Maven.
- `ClinicaApplication.java`: inicia a aplicação e o servidor web.
- `controller/`: recebe as requisições e coordena a validação, as chamadas ao repository, as mensagens e o retorno das páginas.
- `dto/`: recebe os dados dos formulários e declara regras de validação nos fluxos que utilizam esses objetos.
- `model/`: representa os registros retornados pelo banco.
- `repository/`: executa SQL, transforma resultados em objetos e delimita as transações indicadas com `@Transactional`.
- `templates/`: contém as páginas Thymeleaf. Os formulários enviam os dados aos controllers; as listagens exibem os resultados dos repositories.
- `application.properties`: reúne configurações comuns.
- `application-mysql.properties`: configura a conexão a partir das variáveis de ambiente.
- `application-sem-banco.properties`: permite a execução inicial sem conexão ao banco.

O conjunto `ProfissionalSaudeController`, `ProfissionalFormulario`, `ProfissionalSaude` e `ProfissionalSaudeRepository` atende profissionais, médicos e enfermeiros. As páginas correspondentes são `profissionais.html` e `profissional-formulario.html`.

Pacientes utilizam `PacienteController`, `PacienteFormulario`, `Paciente` e `PacienteRepository`, com páginas de listagem, cadastro e edição. Especialidades seguem a mesma organização com o prefixo `Especialidade`; sua listagem inclui a contagem de médicos. Medicamentos utilizam `MedicamentoController`, `Medicamento` e `MedicamentoRepository`, com a página `templates/medicamentos/lista.html`; a integração das validações desse fluxo está em revisão.

## Roteiro de verificação manual

Use dados fictícios. Este roteiro descreve o que conferir; não afirma que todas as evidências já foram anexadas.

1. Cadastre e edite um paciente. Confira nome obrigatório, CPF com 11 dígitos e rejeição de CPF duplicado.
2. Cancele uma exclusão e confira que o registro permanece. Exclua um paciente de teste sem vínculos; tente excluir outro com consulta ou prontuário e confira o bloqueio.
3. Cadastre médico e enfermeiro e confira o mesmo ID entre o profissional base e sua especialização.
4. Tente cadastrar médico com CPF novo e CRM existente. Confira a rejeição e a ausência do novo profissional no banco.
5. Edite nome e CRM de um médico usando CRM existente. Reabra o registro e confira os valores originais.
6. Verifique que o próprio médico não aparece nas opções de supervisor, inclusive após erro de validação ou duplicidade.
7. Tente editar um enfermeiro com CPF de outro profissional. Reabra e confira o CPF original.
8. Exclua completamente um profissional de teste sem vínculos impeditivos. Consulte as tabelas base e de especialização para confirmar a ausência em ambas.
9. Tente excluir médico com consulta e profissional com evolução clínica; confira as mensagens de bloqueio.
10. Cadastre, edite e exclua medicamento de teste sem prescrições. Após a conclusão dos ajustes desse CRUD, confira também nome inválido e exclusão de medicamento vinculado a prescrição.
11. Cadastre e edite especialidade, teste nome vazio e duplicado, confira a contagem de médicos e a exclusão bloqueada quando houver vínculos.

Registre os resultados em [testes da Etapa 2](docs/testes-etapa2.md). Uma mensagem de erro isolada não comprova rollback: confira também os dados persistidos após a operação.

## Problemas comuns

| Sintoma | O que verificar |
| --- | --- |
| Java incompatível | JDK do projeto, do Maven e da execução: todos devem usar Java 25. |
| Imports do Spring em vermelho | Aguarde as dependências ou recarregue o projeto Maven. |
| Porta 8080 ocupada | Pare a outra execução ou altere `server.port` e use a nova porta. |
| `Access denied` | Usuário, senha e permissões no MySQL. |
| `Communications link failure` | MySQL iniciado, host e porta corretos. |
| `Unknown database` | Existência de `clinica_medica` no servidor configurado. |
| Rotas dos CRUDs retornam 404 | Perfil `mysql` ativo e aplicação reiniciada. |
| Tabela não encontrada | Execução do DDL e banco selecionado na conexão. |
| IDE não reconhece tabelas no editor | A inspeção SQL do IntelliJ tem configuração própria; isso não comprova falha na conexão da aplicação. |
| `ClinicaApplication` não encontrada no módulo | Abra `clinica`, importe `pom.xml` e recrie a execução pelo `main`; reconfigure perfil e variáveis. |
| Trigger já existe | Verifique `SHOW TRIGGERS`; não repita o script 04 em uma instalação já configurada. |

## Pendências da Etapa 2

- Concluir e verificar os ajustes de validação e erros de medicamentos.
- Completar as evidências pendentes e manter os links do documento de testes atualizados.
- Conferir manualmente os relatórios parametrizados e a leitura da visão consolidada. Os valores das consultas representam valores registrados, não pagamentos recebidos.
- Implementar e documentar perfis de acesso, privilégios mínimos, VIEW restritiva e os requisitos de autenticação e proteção de dados.
- Consolidar o relatório final e preparar a demonstração.

## Integrantes

- Jean Ribeiro
- Wendy Pereira
- Angelo Roberto
- Yasmin Gomes
- Lucas Pereira



