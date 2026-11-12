package br.edu.clinica.repository;

import br.edu.clinica.model.ConsultaPaciente;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("mysql")
public class MinhasConsultasRepository {

    private final JdbcTemplate jdbc;

    public MinhasConsultasRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Todas as consultas do paciente, da mais antiga para a mais recente. */
    public List<ConsultaPaciente> listarPorPaciente(Integer idPaciente) {
        String sql = """
                SELECT c.data_hora,
                       ps.nome AS medico,
                       (SELECT GROUP_CONCAT(e.nome ORDER BY e.nome SEPARATOR ', ')
                          FROM Medico_Especialidade me
                          JOIN Especialidade e ON e.id_especialidade = me.id_especialidade
                         WHERE me.id_medico = c.id_medico) AS especialidades
                FROM Consulta c
                JOIN Profissional_Saude ps ON ps.id_profissional = c.id_medico
                WHERE c.id_paciente = ?
                ORDER BY c.data_hora
                """;
        return jdbc.query(sql, (rs, n) -> new ConsultaPaciente(
                rs.getObject("data_hora", LocalDateTime.class),
                rs.getString("medico"),
                rs.getString("especialidades")), idPaciente);
    }
}
