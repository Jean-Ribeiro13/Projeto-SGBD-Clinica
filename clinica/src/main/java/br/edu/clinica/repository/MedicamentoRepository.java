package br.edu.clinica.repository;

import br.edu.clinica.model.Medicamento;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("mysql")
public class MedicamentoRepository {

    private final JdbcTemplate jdbc;

    public MedicamentoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Medicamento> listarTodos() {
        String sql = """
                SELECT id_medicamento, nome
                FROM Medicamento
                ORDER BY nome
                """;

        return jdbc.query(sql, (rs, linha) ->
                new Medicamento(
                        rs.getInt("id_medicamento"),
                        rs.getString("nome")
                ));
    }

    public Optional<Medicamento> buscar(int id) {
        String sql = """
                SELECT id_medicamento, nome
                FROM Medicamento
                WHERE id_medicamento = ?
                """;
        return jdbc.query(sql, (rs, linha) -> new Medicamento(
                        rs.getInt("id_medicamento"), rs.getString("nome")), id)
                .stream().findFirst();
    }

    public void cadastrar(String nome) {
        String sql = """
                INSERT INTO Medicamento (nome)
                VALUES (?)
                """;

        jdbc.update(sql, nome);
    }

    public int atualizar(int id, String nome) {
        String sql = """
                UPDATE Medicamento
                SET nome = ?
                WHERE id_medicamento = ?
                """;

        return jdbc.update(sql, nome, id);
    }

    public int excluir(int id) {
        String sql = """
                DELETE FROM Medicamento
                WHERE id_medicamento = ?
                """;

        return jdbc.update(sql, id);
    }
}
