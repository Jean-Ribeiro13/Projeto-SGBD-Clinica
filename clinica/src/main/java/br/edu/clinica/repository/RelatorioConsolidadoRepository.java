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
                SELECT medico.id_medico,
                       medico.nome_medico,
                       medico.crm,
                       medico.especialidades,
                       COUNT(DISTINCT consulta.id_consulta) AS quantidade_consultas,
                       COUNT(DISTINCT consulta.id_paciente) AS quantidade_pacientes,
                       COALESCE(SUM(consulta.quantidade_prescricoes), 0) AS quantidade_prescricoes,
                       COALESCE(SUM(consulta.valor_consulta), 0) AS valor_total,
                       COALESCE(AVG(consulta.valor_consulta), 0) AS valor_medio
                FROM (
                    SELECT DISTINCT id_medico, nome_medico, crm, especialidades
                    FROM vw_relatorio_consolidado
                ) medico
                LEFT JOIN vw_relatorio_consolidado consulta
                       ON consulta.id_medico = medico.id_medico
                      AND consulta.id_consulta IS NOT NULL
                """);
        List<Object> parametros = new ArrayList<>();

        // As datas ficam no ON do LEFT JOIN para o médico continuar aparecendo
        // com totais zerados quando não houver consultas dentro do período.
        if (dataInicial != null) {
            sql.append(" AND consulta.data_hora >= ?");
            parametros.add(dataInicial.atStartOfDay());
        }
        if (dataFinal != null) {
            sql.append(" AND consulta.data_hora < ?");
            parametros.add(dataFinal.plusDays(1).atStartOfDay());
        }
        if (idMedico != null) {
            sql.append(" WHERE medico.id_medico = ?");
            parametros.add(idMedico);
        }

        sql.append("""
                 GROUP BY medico.id_medico, medico.nome_medico, medico.crm, medico.especialidades
                 ORDER BY medico.nome_medico
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
