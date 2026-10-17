# Testes manuais — Etapa 2

| Teste | Resultado observado | Evidência |
| --- | --- | --- |
| Cadastro de médico | Profissional e médico cadastrados com o mesmo ID | |
| Cadastro com CRM duplicado | Mensagem de erro e rollback do novo profissional | |
| Edição com CRM duplicado | Nome e CRM originais preservados no banco | |
| Exclusão de médico com consulta | Operação bloqueada com mensagem na interface | |
| Cadastro e edição de enfermeiro | Dados gravados e recuperados corretamente | |
| Remoção da especialização de enfermeiro | Profissional mantido no banco | |
| Exclusão de profissional com evolução clínica | Operação bloqueada | |
| CPF duplicado no cadastro e edição de profissional | Mensagem de erro e campos preservados | |
