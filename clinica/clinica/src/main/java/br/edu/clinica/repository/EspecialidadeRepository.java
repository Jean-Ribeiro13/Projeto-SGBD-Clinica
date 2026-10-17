package br.edu.clinica.repository;

import br.edu.clinica.model.Especialidade;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("mysql")
public class EspecialidadeRepository {
    private final JdbcTemplate jdbc;

    public EspecialidadeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Especialidade map(java.sql.ResultSet rs, int row) throws java.sql.SQLException {
        return new Especialidade(rs.getInt("id_especialidade"), rs.getString("nome"), rs.getInt("qtd_medicos"));
    }

    /** Junção + agregação: quantos médicos possuem cada especialidade. */
    public List<Especialidade> listarTodas() {
        String sql = """
                SELECT e.id_especialidade, e.nome, COUNT(me.id_medico) AS qtd_medicos
                FROM Especialidade e
                LEFT JOIN Medico_Especialidade me ON me.id_especialidade = e.id_especialidade
                GROUP BY e.id_especialidade, e.nome
                ORDER BY e.nome
                """;
        return jdbc.query(sql, this::map);
    }

    public Optional<Especialidade> buscar(Integer id) {
        String sql = "SELECT id_especialidade, nome, 0 AS qtd_medicos FROM Especialidade WHERE id_especialidade = ?";
        return jdbc.query(sql, this::map, id).stream().findFirst();
    }

    public void criar(String nome) {
        jdbc.update("INSERT INTO Especialidade(nome) VALUES (?)", nome);
    }

    public int atualizar(Integer id, String nome) {
        return jdbc.update("UPDATE Especialidade SET nome=? WHERE id_especialidade=?", nome, id);
    }

    public int excluir(Integer id) {
        return jdbc.update("DELETE FROM Especialidade WHERE id_especialidade=?", id);
    }
}