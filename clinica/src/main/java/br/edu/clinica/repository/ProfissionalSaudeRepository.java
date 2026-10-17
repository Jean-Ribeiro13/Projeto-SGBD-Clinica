package br.edu.clinica.repository;

import br.edu.clinica.dto.ProfissionalFormulario;
import br.edu.clinica.model.ProfissionalSaude;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("mysql")
public class ProfissionalSaudeRepository {
    private final JdbcTemplate jdbc;

    public ProfissionalSaudeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SELECT = """
            SELECT p.id_profissional, p.nome, p.cpf, p.data_nascimento,
                   CASE WHEN m.id_profissional IS NOT NULL THEN 'Médico'
                        WHEN e.id_profissional IS NOT NULL THEN 'Enfermeiro' ELSE 'Profissional' END AS tipo,
                   COALESCE(m.crm, e.coren) AS registro, m.id_supervisor, sup.nome AS supervisor_nome
            FROM Profissional_Saude p
            LEFT JOIN Medico m ON m.id_profissional = p.id_profissional
            LEFT JOIN Enfermeiro e ON e.id_profissional = p.id_profissional
            LEFT JOIN Medico sm ON sm.id_profissional = m.id_supervisor
            LEFT JOIN Profissional_Saude sup ON sup.id_profissional = sm.id_profissional
            """;

    private ProfissionalSaude map(java.sql.ResultSet rs, int row) throws java.sql.SQLException {
        return new ProfissionalSaude(rs.getInt("id_profissional"), rs.getString("nome"),
                rs.getString("cpf"), rs.getObject("data_nascimento", LocalDate.class),
                rs.getString("tipo"), rs.getString("registro"),
                (Integer) rs.getObject("id_supervisor"), rs.getString("supervisor_nome"));
    }

    public List<ProfissionalSaude> listarTodos() {
        return jdbc.query(SELECT + " ORDER BY p.nome", this::map);
    }

    public List<ProfissionalSaude> listarMedicos() {
        return jdbc.query(SELECT + " WHERE m.id_profissional IS NOT NULL ORDER BY p.nome", this::map);
    }

    public List<ProfissionalSaude> listarEnfermeiros() {
        return jdbc.query(SELECT + " WHERE e.id_profissional IS NOT NULL ORDER BY p.nome", this::map);
    }

    public List<ProfissionalSaude> listarMedicosSem(Integer id) {
        String sql = SELECT + " WHERE m.id_profissional IS NOT NULL AND (? IS NULL OR m.id_profissional <> ?) ORDER BY p.nome";
        return jdbc.query(sql, this::map, id, id);
    }

    public Optional<ProfissionalSaude> buscar(Integer id) {
        return jdbc.query(SELECT + " WHERE p.id_profissional = ?", this::map, id).stream().findFirst();
    }

    public boolean existeMedico(Integer id) {
        return Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM Medico WHERE id_profissional = ?)", Boolean.class, id));
    }

    public boolean existeEnfermeiro(Integer id) {
        return Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM Enfermeiro WHERE id_profissional = ?)", Boolean.class, id));
    }

    @Transactional
    public int criar(ProfissionalFormulario f, String tipo) {

        if (!"medico".equals(tipo) && !"enfermeiro".equals(tipo)) {
            throw new IllegalArgumentException(
                    "O profissional deve ser cadastrado como médico ou enfermeiro."
            );
        }
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbc.update(c -> {
            var ps = c.prepareStatement("INSERT INTO Profissional_Saude(nome, cpf, data_nascimento) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, f.getNome());
            ps.setString(2, f.getCpf());
            ps.setObject(3, f.getDataNascimento());
            return ps;
        }, key);
        int id = key.getKey().intValue();
        if ("medico".equals(tipo))
            jdbc.update("INSERT INTO Medico(id_profissional, crm, id_supervisor) VALUES (?, ?, ?)", id, f.getRegistro(), f.getSupervisorId());
        if ("enfermeiro".equals(tipo))
            jdbc.update("INSERT INTO Enfermeiro(id_profissional, coren) VALUES (?, ?)", id, f.getRegistro());
        return id;
    }

    @Transactional
    public int atualizar(Integer id, ProfissionalFormulario f, String tipo) {
        int n = jdbc.update("UPDATE Profissional_Saude SET nome=?, cpf=?, data_nascimento=? WHERE id_profissional=?", f.getNome(), f.getCpf(), f.getDataNascimento(), id);
        if ("medico".equals(tipo))
            jdbc.update("UPDATE Medico SET crm=?, id_supervisor=? WHERE id_profissional=?", f.getRegistro(), f.getSupervisorId(), id);
        else if ("enfermeiro".equals(tipo))
            jdbc.update("UPDATE Enfermeiro SET coren=? WHERE id_profissional=?", f.getRegistro(), id);
        return n;
    }

    public int excluirProfissional(Integer id) {
        return jdbc.update("DELETE FROM Profissional_Saude WHERE id_profissional=?", id);
    }
}
