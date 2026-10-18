package br.edu.clinica.repository;

import br.edu.clinica.model.RelatorioConsolidadoMedico;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("mysql")
public class RelatorioConsolidadoRepository {

    private final JdbcTemplate jdbc;

    public RelatorioConsolidadoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<RelatorioConsolidadoMedico> consultar(
            Integer idMedico,
            LocalDate dataInicial,
            LocalDate dataFinal) {

        StringBuilder sql = new StringBuilder("""
                SELECT id_medico,
                       nome_medico,
                       crm,
                       especialidades,
                       COUNT(DISTINCT id_consulta) AS quantidade_consultas,
                       COUNT(DISTINCT id_paciente) AS quantidade_pacientes,
                       COALESCE(SUM(quantidade_prescricoes), 0) AS quantidade_prescricoes,
                       COALESCE(SUM(valor_consulta), 0) AS valor_total,
                       COALESCE(AVG(valor_consulta), 0) AS valor_medio
                FROM vw_relatorio_consolidado
                WHERE 1 = 1
                """);
        List<Object> parametros = new ArrayList<>();

        if (dataInicial != null) {
            sql.append(" AND (data_hora IS NULL OR data_hora >= ?)");
            parametros.add(dataInicial.atStartOfDay());
        }
        if (dataFinal != null) {
            sql.append(" AND (data_hora IS NULL OR data_hora < ?)");
            parametros.add(dataFinal.plusDays(1).atStartOfDay());
        }
        if (idMedico != null) {
            sql.append(" AND id_medico = ?");
            parametros.add(idMedico);
        }

        sql.append("""
                 GROUP BY id_medico, nome_medico, crm, especialidades
                 ORDER BY nome_medico
                """);

        return jdbc.query(sql.toString(), (rs, linha) -> new RelatorioConsolidadoMedico(
                rs.getInt("id_medico"),
                rs.getString("nome_medico"),
                rs.getString("crm"),
                rs.getString("especialidades"),
                rs.getLong("quantidade_consultas"),
                rs.getLong("quantidade_pacientes"),
                rs.getLong("quantidade_prescricoes"),
                rs.getBigDecimal("valor_total"),
                rs.getBigDecimal("valor_medio")
        ), parametros.toArray());
    }
}
