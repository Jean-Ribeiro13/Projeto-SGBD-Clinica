# Clínica — configuração inicial com Spring Boot

Projeto didático inicial: uma página web e um teste opcional de conexão ao MySQL. Ainda não contém os CRUDs, login ou os demais requisitos da Etapa 2.

## Tecnologias

- JDK 25
- Spring Boot 4.1.1
- Maven (pode usar o Maven integrado ao IntelliJ)
- Spring Web MVC, Thymeleaf, Spring JDBC e MySQL Connector/J

## 1. Abrir no IntelliJ IDEA

1. Se recebeu o ZIP, extraia-o primeiro.
2. No IntelliJ, escolha **Open** e selecione a pasta `clinica`, que contém `pom.xml`.
3. Se solicitado, abra como projeto Maven. Aguarde o download das dependências.
4. Em **File > Project Structure > Project**, selecione o JDK 25 no campo **SDK**.
5. Neste computador, o JDK foi encontrado em `C:\Program Files\Java\zulu25.36.15-ca-jdk25.0.4-win_x64`. Se ele não aparecer, use **Add SDK > JDK from disk** e selecione essa pasta.
6. Nas configurações de Maven do IntelliJ, mantenha o Maven integrado e use o JDK do projeto para importação e execução.

O terminal deste computador apontava para Java 8 na preparação deste projeto. Se executar pela IDE com JDK 25, não é necessário alterar o PATH do Windows agora.

## 2. Executar primeiro sem banco

1. Abra `src/main/java/br/edu/clinica/ClinicaApplication.java`.
2. Clique no triângulo verde ao lado de `main` e escolha **Run**.
3. Aguarde a mensagem `Started ClinicaApplication` no console.
4. Acesse `http://localhost:8080` no navegador.

Resultado esperado: **O Spring Boot está funcionando!**

O perfil padrão `sem-banco` desativa a configuração de conexão nesta primeira execução. A aplicação escuta apenas no computador local.

## 3. Ativar a conexão MySQL

Confirme no Workbench que o banco existe:

```sql
SHOW DATABASES LIKE 'clinica_medica';
```

Se o banco ainda não existir, execute os scripts originais `01_ddl.sql` e depois `02_dml.sql`, verificando erros entre eles. Se ele já existe e está preenchido, não repita a criação ou a carga.

No IntelliJ:

1. Pare a aplicação pelo botão vermelho.
2. Abra **Run > Edit Configurations** e selecione a configuração de `ClinicaApplication`.
3. Se necessário, use **Modify options** para mostrar **Environment variables**.
4. No editor de variáveis, adicione uma por linha:

| Nome | Valor |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `mysql` |
| `DB_USER` | Seu usuário do MySQL |
| `DB_PASSWORD` | A senha desse usuário |
| `DB_URL` | `jdbc:mysql://localhost:3306/clinica_medica` |

Use o host e a porta da conexão do Workbench caso sejam diferentes. Use a senha do MySQL, não a senha do Windows. Não coloque credenciais no código nem compartilhe a configuração de execução com a opção de salvá-la como arquivo do projeto.

5. Salve e execute novamente.
6. Abra `http://localhost:8080/teste-banco`.

Resultado esperado: **Conexão com o MySQL funcionando!**

Essa rota executa somente `SELECT 1`. Ela verifica o acesso ao banco selecionado, mas não valida as tabelas nem a carga. A página inicial, isoladamente, também não comprova a conexão.

## Como os arquivos se conectam

- `pom.xml`: lista as bibliotecas que o Maven baixa e define a versão do Java.
- `ClinicaApplication.java`: inicia o servidor e a aplicação.
- `InicioController.java`: recebe a requisição à página inicial e retorna o nome do HTML.
- `templates/inicio.html`: conteúdo exibido no navegador.
- `BancoController.java`: testa a conexão, disponível apenas no perfil `mysql`.
- `application.properties`: configurações comuns.
- `application-sem-banco.properties`: configurações da primeira execução.
- `application-mysql.properties`: lê as credenciais das variáveis de ambiente.

## Problemas comuns

| Sintoma | O que verificar |
| --- | --- |
| Java incompatível ou `release version 25 not supported` | SDK do projeto, JDK usado pelo Maven e JRE da configuração de execução devem apontar para o JDK 25. |
| Imports do Spring em vermelho | Aguarde o Maven ou use a opção de recarregar os projetos Maven. O primeiro download precisa de internet. |
| Porta 8080 ocupada | Pare a outra execução ou altere `server.port` para `8081` e use essa porta no navegador. |
| `Access denied` | Confira usuário, senha e permissões de acesso ao banco. |
| `Communications link failure` | Confira se o MySQL está iniciado e se host e porta estão corretos. |
| `Unknown database` | Confira se `clinica_medica` foi criado na mesma conexão. |
| `/teste-banco` retorna 404 | Confira se a variável `SPRING_PROFILES_ACTIVE=mysql` está na configuração executada e reinicie. |

## Estado de verificação

Os arquivos foram revisados e o JDK 25 local foi identificado. O projeto não foi compilado nem executado durante a preparação: o ambiente não permitiu baixar dependências pela rede. A conexão MySQL depende das credenciais que você configurará localmente.

## Próximo passo

Após confirmar a página inicial e a conexão, implementar a listagem de pacientes com Spring JDBC e Thymeleaf. Depois, acrescentar cadastro, edição e exclusão.
