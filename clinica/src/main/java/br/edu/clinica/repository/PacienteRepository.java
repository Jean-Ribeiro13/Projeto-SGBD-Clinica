package br.edu.clinica.repository;

import br.edu.clinica.model.Paciente;
import java.time.LocalDate;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import br.edu.clinica.dto.PacienteFormulario;

/*
    Esse arquivo concentra o acesso à tabela 'Paciente'.

    Ele utiliza o JdbcTemplate, que auxilia na execução do SQL e no processamento dos resultados.

    Para cada linha retornada, o codigo lê as colunas e cria um objeto 'Paciente'. Ao terminar,
    devolve uma List<Paciente> para quem chamou o método.

    A anotação @Repository identifica essa classe como um componente de acesso a dados. O @Profile("mysql")
    faz com que ela seja ativada quando a aplicação estiver usando o perfil mysql.
 */

@Repository
@Profile("mysql")
public class PacienteRepository {
    private final JdbcTemplate jdbc;

    public PacienteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Paciente> listarTodos() {
        String sql = """
                SELECT id_paciente, nome, cpf, data_nascimento
                FROM Paciente
                ORDER BY nome, id_paciente
                """;

        // Cria um PreparedStatement e converte cada linha em um Paciente.
        // Esta consulta nao possui filtros nem parametros de entrada.
        return jdbc.query(conexao -> conexao.prepareStatement(sql), (rs, numeroLinha) ->
                new Paciente(
                        rs.getInt("id_paciente"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getObject("data_nascimento", LocalDate.class)
                ));
    }

    public void cadastrar(PacienteFormulario formulario) {

        // INSERT aqui serve para informar qual tabela vai ser inserido as informações
        String sql = """
                INSERT INTO Paciente (nome, cpf, data_nascimento) 
                VALUES (?, ?, ?)
                """;

        jdbc.update( // Executa comandos que alteram dados, como INSERT, UPDATE e DELETE. Apesar do nome, ele também serve para cadastrar.
                sql,
                formulario.getNome(),
                formulario.getCpf(),
                formulario.getDataNascimento()

                // Importante lembrar que a ordem dos valores precisa de corresponder a ordem das colunas também
        );
    }
}
