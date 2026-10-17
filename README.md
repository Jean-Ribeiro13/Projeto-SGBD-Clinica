# Clínica Médica — Spring Boot e MySQL

Projeto acadêmico de banco de dados para uma clínica médica, com aplicação web desenvolvida em Java, Spring Boot e MySQL.

A aplicação possui CRUD de pacientes e cadastros de profissionais de saúde, médicos e enfermeiros. Os cadastros de médico e enfermeiro criam o respectivo registro base em `Profissional_Saude`; médicos também podem informar um supervisor. As demais entidades do banco ainda não possuem telas CRUD.

## Funcionalidades implementadas

- Página inicial com acesso aos pacientes, profissionais de saúde, médicos e enfermeiros.
- Listagem de pacientes em ordem alfabética.
- Cadastro e edição de nome, CPF e data de nascimento.
- Exclusão com confirmação.
- Validação de nome obrigatório, com até 100 caracteres.
- Validação do formato do CPF: exatamente 11 dígitos, sem pontuação.
- Tratamento de CPF duplicado, mantendo os dados preenchidos no formulário.
- Mensagens de sucesso após cadastro, edição e exclusão.
- Tratamento de exclusões bloqueadas por registros vinculados.
- Listagem, cadastro, edição e exclusão de profissionais de saúde.
- Cadastro, listagem, edição e remoção da especialização de médico, com CRM e supervisor opcional.
- Cadastro, listagem, edição e remoção da especialização de enfermeiro, com COREN.
- Ao remover médico ou enfermeiro, o registro base do profissional é mantido.
- Teste de conexão com o MySQL.

A data de nascimento é opcional. A validação do CPF verifica o formato, mas não calcula os dígitos verificadores.

O banco impede a exclusão de pacientes com consultas ou prontuário vinculado. Quando a exclusão é permitida, os contatos de emergência associados são removidos em cascata.

## Tecnologias

- Java — JDK 25
- Spring Boot 4.1.1
- Maven
- Spring Web MVC
- Thymeleaf
- Spring JDBC
- Bean Validation
- MySQL Connector/J
- MySQL 8.0.16 ou superior

O acesso ao banco utiliza SQL explícito e parâmetros nas operações que recebem dados do usuário. As tabelas são criadas pelos scripts SQL.

## Estrutura do repositório

```text
repositorio/
├── .gitignore
├── README.md
├── sql/
│   ├── 01_ddl.sql
│   ├── 02_dml.sql
│   └── 03_consultas.sql
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

Instale:

- JDK 25.
- MySQL Server.
- IntelliJ IDEA.
- MySQL Workbench ou outro cliente para executar os scripts.

O Maven integrado ao IntelliJ pode ser utilizado. É necessário acesso à internet para baixar as dependências na primeira abertura.

### 2. Criar e preencher o banco

Abra uma conexão com o MySQL e execute os arquivos da pasta `sql` nesta ordem:

1. `01_ddl.sql`: cria o banco `clinica_medica`, as tabelas e suas restrições.
2. `02_dml.sql`: insere os dados de exemplo.
3. `03_consultas.sql`: executa as consultas de leitura para explorar os dados.

Confira se cada arquivo terminou sem erros antes de executar o seguinte.

Os scripts de criação e carga devem ser executados em uma base nova. Eles não foram preparados para reexecução sobre um banco já preenchido.

Para conferir a carga inicial:

```sql
USE clinica_medica;

SELECT COUNT(*) AS total_pacientes
FROM Paciente;
```

Após a carga original, o resultado esperado é 40 pacientes. Esse total pode mudar conforme a aplicação for utilizada.

Os scripts reproduzem a estrutura e os dados de exemplo. Eles não incluem automaticamente alterações feitas posteriormente pela aplicação.

### 3. Abrir o projeto no IntelliJ

1. Clone o repositório ou baixe e extraia o ZIP.
2. No IntelliJ, escolha **Open**.
3. Selecione a pasta `clinica`, que contém o arquivo `pom.xml`.
4. Abra como projeto Maven, se solicitado.
5. Aguarde o carregamento das dependências.
6. Em **File > Project Structure > Project**, selecione o JDK 25.
7. Confira se a importação e a execução do Maven também utilizam o JDK 25.

Caso o JDK não apareça na lista, adicione a pasta onde ele foi instalado no seu computador.

### 4. Configurar a conexão com o banco

Abra a configuração de execução de `ClinicaApplication` em **Run > Edit Configurations**.

No campo **Active profiles**, informe:

```text
mysql
```

Se esse campo não estiver disponível, adicione a variável de ambiente `SPRING_PROFILES_ACTIVE` com o valor `mysql`.

Em **Environment variables**, configure:

| Variável | Valor |
| --- | --- |
| `DB_USER` | Seu usuário do MySQL |
| `DB_PASSWORD` | A senha desse usuário |
| `DB_URL` | `jdbc:mysql://localhost:3306/clinica_medica` |

Ajuste o host e a porta caso sua instalação utilize valores diferentes.

As credenciais devem ser configuradas localmente por cada pessoa. Não coloque senhas nos arquivos versionados nem compartilhe configurações de execução que contenham credenciais.

### 5. Iniciar a aplicação

1. Abra `ClinicaApplication.java`.
2. Execute o método `main` pelo botão **Run**.
3. Aguarde a mensagem `Started ClinicaApplication` no console.
4. Acesse [a página inicial](http://localhost:8080).
5. Clique em **Ver pacientes**.

Endereços disponíveis:

| Endereço | Função |
| --- | --- |
| `http://localhost:8080/` | Página inicial |
| `http://localhost:8080/pacientes` | Listagem e acesso ao CRUD de pacientes |
| `http://localhost:8080/pacientes/novo` | Formulário de cadastro de paciente |
| `http://localhost:8080/profissionais` | Listagem e CRUD de profissionais de saúde |
| `http://localhost:8080/medicos` | Listagem e CRUD de médicos |
| `http://localhost:8080/enfermeiros` | Listagem e CRUD de enfermeiros |
| `http://localhost:8080/teste-banco` | Teste de conexão com o MySQL |

A rota de teste executa apenas `SELECT 1`. Ela verifica a conexão, mas não confirma a existência das tabelas ou a carga dos dados.

O perfil padrão `sem-banco` permite abrir a página inicial sem configurar o MySQL. Para utilizar o CRUD, ative o perfil `mysql`.

## Organização do código

- **Controller:** recebe as requisições, valida os dados recebidos e define a página ou o redirecionamento.
- **DTO:** transporta os dados dos formulários e declara as regras de validação.
- **Model:** representa os registros consultados no banco.
- **Repository:** executa os comandos SQL e transforma os resultados em objetos Java.
- **Templates:** páginas HTML preenchidas pelo Thymeleaf.

No CRUD de profissionais de saúde:

- `ProfissionalSaude.java` representa o profissional e, quando aplicável, seus dados de médico ou enfermeiro.
- `ProfissionalFormulario.java` recebe os campos do formulário e declara as regras de validação.
- `ProfissionalSaudeRepository.java` executa as operações SQL para profissionais, médicos e enfermeiros.
- `ProfissionalSaudeController.java` conecta essas operações às rotas e páginas.
- `profissionais.html` exibe a listagem; `profissional-formulario.html` é usado para cadastro e edição.

No CRUD de pacientes:

- `Paciente.java` representa um paciente.
- `PacienteFormulario.java` recebe os campos de cadastro e edição.
- `PacienteRepository.java` executa consultas, inserções, atualizações e exclusões.
- `PacienteController.java` conecta as operações às páginas.
- `pacientes.html` apresenta a listagem.
- `paciente-formulario.html` apresenta o cadastro.
- `paciente-edicao.html` apresenta a edição.

## Verificação manual

Para conferir a aplicação em outro computador:

1. Abra a listagem e confira os pacientes da carga.
2. Cadastre um paciente fictício.
3. Edite seu nome e confira a alteração.
4. Tente cadastrar um CPF já existente.
5. Tente enviar um nome composto apenas por espaços.
6. Tente enviar um CPF com menos de 11 dígitos.
7. Cancele uma exclusão e confira que o registro permanece.
8. Exclua um paciente de teste sem consultas ou prontuário.
9. Tente excluir um paciente com consulta vinculada e confira a mensagem de bloqueio.

Esses fluxos foram testados manualmente durante o desenvolvimento. Ainda não há uma suíte de testes automatizados.

## Problemas comuns

| Sintoma | O que verificar |
| --- | --- |
| Java incompatível | Confira o JDK do projeto, do Maven e da execução. O projeto utiliza Java 25. |
| Imports do Spring em vermelho | Aguarde o Maven ou recarregue o projeto Maven. |
| Porta 8080 ocupada | Pare a outra execução ou altere `server.port` e utilize a nova porta no navegador. |
| `Access denied` | Confira usuário, senha e permissões no MySQL. |
| `Communications link failure` | Confira se o MySQL está iniciado e se host e porta estão corretos. |
| `Unknown database` | Confira se `clinica_medica` existe no servidor configurado. |
| Rota de pacientes retorna 404 | Confira se o perfil `mysql` está ativo e reinicie a aplicação. |
| Tabela não encontrada durante a execução | Confira a execução do DDL e o banco selecionado na conexão. |
| IntelliJ não reconhece a tabela no editor | A conexão usada pela inspeção SQL da IDE pode não estar configurada, mesmo que a aplicação funcione. |

## Próximas etapas

- Implementar CRUDs para especialidades, contatos de emergência, prontuários, consultas, evoluções clínicas, medicamentos e prescrições.
- Criar consultas parametrizadas com filtros, junções e agregações.
- Criar relatório baseado em uma visão do banco.
- Implementar uma operação transacional envolvendo múltiplas tabelas.
- Desenvolver autenticação e controle de acesso.
- Implementar auditoria por gatilhos.
- Realizar os experimentos de concorrência e desempenho.
- Consolidar a documentação exigida na Etapa 2.

## Integrantes

- Jean Ribeiro
- Wendy Pereira
- Angelo Roberto
- Yasmin Gomes
- Lucas Pereira
