package br.edu.clinica.repository;

import br.edu.clinica.model.ResumoConsultasMedico;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Profile("mysql")
public class ResumoConsultasRepository {

    private final JdbcTemplate jdbc;

    public ResumoConsultasRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ResumoConsultasMedico> consultar(
            Integer idMedico,
            LocalDate dataInicial,
            LocalDate dataFinal) {

        String sql = """
                SELECT
                    m.id_profissional,
                    p.nome AS nome_medico,
                    m.crm,
                    COUNT(c.id_consulta) AS quantidade_consultas,
                    COALESCE(SUM(c.valor), 0) AS valor_total
                FROM Medico m
                JOIN Profissional_Saude p
                    ON p.id_profissional = m.id_profissional
                LEFT JOIN Consulta c
                    ON c.id_medico = m.id_profissional
                    AND c.data_hora >= ?
                    AND c.data_hora < ?
                WHERE (? IS NULL OR m.id_profissional = ?)
                GROUP BY m.id_profissional, p.nome, m.crm
                ORDER BY p.nome
                """;

        return jdbc.query(
                sql,
                (rs, linha) -> new ResumoConsultasMedico(
                        rs.getInt("id_profissional"),
                        rs.getString("nome_medico"),
                        rs.getString("crm"),
                        rs.getLong("quantidade_consultas"),
                        rs.getBigDecimal("valor_total")
                ),
                dataInicial.atStartOfDay(),
                dataFinal.plusDays(1).atStartOfDay(),
                idMedico,
                idMedico
        );
    }
}