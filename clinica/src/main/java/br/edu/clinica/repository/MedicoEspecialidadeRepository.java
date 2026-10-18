package br.edu.clinica.repository;

import br.edu.clinica.model.EspecialidadeOpcao;
import br.edu.clinica.model.MedicoEspecialidade;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Profile("mysql")
public class MedicoEspecialidadeRepository {
    private static final String SELECT_VINCULOS = """
            SELECT me.id_medico,
                   profissional.nome AS nome_medico,
                   medico.crm,
                   me.id_especialidade,
                   especialidade.nome AS nome_especialidade,
                   me.data_obtencao
            FROM Medico_Especialidade me
            JOIN Medico medico
              ON medico.id_profissional = me.id_medico
            JOIN Profissional_Saude profissional
              ON profissional.id_profissional = medico.id_profissional
            JOIN Especialidade especialidade
              ON especialidade.id_especialidade = me.id_especialidade
            """;

    private final JdbcTemplate jdbc;

    public MedicoEspecialidadeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<MedicoEspecialidade> listarTodos() {
        return jdbc.query(SELECT_VINCULOS + " ORDER BY profissional.nome, especialidade.nome",
                (rs, linha) -> new MedicoEspecialidade(
                        rs.getInt("id_medico"),
                        rs.getString("nome_medico"),
                        rs.getString("crm"),
                        rs.getInt("id_especialidade"),
                        rs.getString("nome_especialidade"),
                        rs.getObject("data_obtencao", LocalDate.class)
                ));
    }

    public List<EspecialidadeOpcao> listarEspecialidades() {
        return jdbc.query("SELECT id_especialidade, nome FROM Especialidade ORDER BY nome",
                (rs, linha) -> new EspecialidadeOpcao(
                        rs.getInt("id_especialidade"),
                        rs.getString("nome")
                ));
    }

    public int cadastrar(Integer idMedico, Integer idEspecialidade, LocalDate dataObtencao) {
        return jdbc.update("""
                        INSERT INTO Medico_Especialidade (id_medico, id_especialidade, data_obtencao)
                        VALUES (?, ?, ?)
                        """,
                idMedico, idEspecialidade, dataObtencao);
    }

    public int atualizarData(Integer idMedico, Integer idEspecialidade, LocalDate dataObtencao) {
        return jdbc.update("""
                        UPDATE Medico_Especialidade
                        SET data_obtencao = ?
                        WHERE id_medico = ? AND id_especialidade = ?
                        """,
                dataObtencao, idMedico, idEspecialidade);
    }

    public int excluir(Integer idMedico, Integer idEspecialidade) {
        return jdbc.update("""
                        DELETE FROM Medico_Especialidade
                        WHERE id_medico = ? AND id_especialidade = ?
                        """,
                idMedico, idEspecialidade);
    }
}
