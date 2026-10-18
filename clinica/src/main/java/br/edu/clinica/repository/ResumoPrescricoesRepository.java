package br.edu.clinica.repository;

import br.edu.clinica.model.ResumoPrescricoesMedicamento;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Profile("mysql")
public class ResumoPrescricoesRepository {

    private final JdbcTemplate jdbc;

    public ResumoPrescricoesRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ResumoPrescricoesMedicamento> consultar(
            Integer idMedicamento,
            LocalDate dataInicial,
            LocalDate dataFinal) {

        String sql = """
                SELECT
                    m.id_medicamento,
                    m.nome AS nome_medicamento,
                    COUNT(pr.id_prescricao) AS quantidade_prescricoes,
                    COUNT(DISTINCT c.id_paciente) AS quantidade_pacientes
                FROM Medicamento m
                LEFT JOIN (
                    Prescricao pr
                    INNER JOIN Consulta c
                        ON c.id_consulta = pr.id_consulta
                )
                    ON pr.id_medicamento = m.id_medicamento
                    AND c.data_hora >= ?
                    AND c.data_hora < ?
                WHERE (? IS NULL OR m.id_medicamento = ?)
                GROUP BY m.id_medicamento, m.nome
                ORDER BY quantidade_prescricoes DESC, m.nome
                """;

        return jdbc.query(
                sql,
                (rs, linha) -> new ResumoPrescricoesMedicamento(
                        rs.getInt("id_medicamento"),
                        rs.getString("nome_medicamento"),
                        rs.getLong("quantidade_prescricoes"),
                        rs.getLong("quantidade_pacientes")
                ),
                dataInicial.atStartOfDay(),
                dataFinal.plusDays(1).atStartOfDay(),
                idMedicamento,
                idMedicamento
        );
    }
}
