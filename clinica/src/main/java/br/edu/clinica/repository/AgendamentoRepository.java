package br.edu.clinica.repository;

import br.edu.clinica.model.EspecialidadeOpcao;
import br.edu.clinica.model.MedicoOpcao;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/*
    Acesso aos dados do agendamento feito pelo paciente.

    A tabela Consulta já garante, pela restrição uq_medico_data_hora (RN16), que um médico
    não tenha duas consultas no mesmo horário. Mesmo assim, os horários livres são calculados
    aqui para mostrar ao paciente só o que está disponível.
 */
@Repository
@Profile("mysql")
public class AgendamentoRepository {

    // Atendimento de segunda a sexta, de 30 em 30 minutos: 08:00-12:00 e 14:00-18:00.
    private static final List<LocalTime> GRADE = montarGrade();

    private final JdbcTemplate jdbc;

    public AgendamentoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static List<LocalTime> montarGrade() {
        List<LocalTime> grade = new ArrayList<>();
        for (LocalTime h = LocalTime.of(8, 0); h.isBefore(LocalTime.of(12, 0)); h = h.plusMinutes(30)) {
            grade.add(h);
        }
        for (LocalTime h = LocalTime.of(14, 0); h.isBefore(LocalTime.of(18, 0)); h = h.plusMinutes(30)) {
            grade.add(h);
        }
        return List.copyOf(grade);
    }

    /** Somente especialidades que têm pelo menos um médico vinculado. */
    public List<EspecialidadeOpcao> listarEspecialidades() {
        String sql = """
                SELECT DISTINCT e.id_especialidade, e.nome
                FROM Especialidade e
                JOIN Medico_Especialidade me ON me.id_especialidade = e.id_especialidade
                ORDER BY e.nome
                """;
        return jdbc.query(sql, (rs, n) ->
                new EspecialidadeOpcao(rs.getInt("id_especialidade"), rs.getString("nome")));
    }

    /** Médicos da especialidade informada; com idEspecialidade nulo, devolve todos os médicos. */
    public List<MedicoOpcao> listarMedicos(Integer idEspecialidade) {
        if (idEspecialidade == null) {
            String sql = """
                    SELECT m.id_profissional, ps.nome
                    FROM Medico m
                    JOIN Profissional_Saude ps ON ps.id_profissional = m.id_profissional
                    ORDER BY ps.nome
                    """;
            return jdbc.query(sql, (rs, n) ->
                    new MedicoOpcao(rs.getInt("id_profissional"), rs.getString("nome")));
        }
        String sql = """
                SELECT DISTINCT m.id_profissional, ps.nome
                FROM Medico m
                JOIN Profissional_Saude ps ON ps.id_profissional = m.id_profissional
                JOIN Medico_Especialidade me ON me.id_medico = m.id_profissional
                WHERE me.id_especialidade = ?
                ORDER BY ps.nome
                """;
        return jdbc.query(sql, (rs, n) ->
                new MedicoOpcao(rs.getInt("id_profissional"), rs.getString("nome")), idEspecialidade);
    }

    public Optional<String> buscarNomeMedico(Integer idMedico) {
        String sql = """
                SELECT ps.nome
                FROM Medico m
                JOIN Profissional_Saude ps ON ps.id_profissional = m.id_profissional
                WHERE m.id_profissional = ?
                """;
        return jdbc.query(sql, (rs, n) -> rs.getString("nome"), idMedico).stream().findFirst();
    }

    /** O paciente se identifica pelo CPF e pela data de nascimento, ambos já cadastrados. */
    public Optional<Integer> buscarPaciente(String cpf, LocalDate dataNascimento) {
        String sql = """
                SELECT id_paciente
                FROM Paciente
                WHERE cpf = ? AND data_nascimento = ?
                """;
        return jdbc.query(sql, (rs, n) -> rs.getInt("id_paciente"), cpf, dataNascimento)
                .stream().findFirst();
    }

    /** Horários livres do médico no dia. Fim de semana e datas passadas não têm horários. */
    public List<LocalTime> horariosLivres(Integer idMedico, LocalDate data) {
        DayOfWeek dia = data.getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY || data.isBefore(LocalDate.now())) {
            return List.of();
        }

        String sql = """
                SELECT data_hora
                FROM Consulta
                WHERE id_medico = ? AND data_hora >= ? AND data_hora < ?
                """;
        List<LocalTime> ocupados = jdbc.query(
                sql,
                (rs, n) -> rs.getObject("data_hora", LocalDateTime.class).toLocalTime(),
                idMedico, data.atStartOfDay(), data.plusDays(1).atStartOfDay());

        boolean hoje = data.equals(LocalDate.now());
        LocalTime agora = LocalTime.now();

        return GRADE.stream()
                .filter(h -> !ocupados.contains(h))
                .filter(h -> !hoje || h.isAfter(agora))
                .toList();
    }

    public void agendar(Integer idPaciente, Integer idMedico, LocalDateTime dataHora, BigDecimal valor) {
        String sql = """
                INSERT INTO Consulta (id_paciente, id_medico, data_hora, valor)
                VALUES (?, ?, ?, ?)
                """;
        jdbc.update(sql, idPaciente, idMedico, dataHora, valor);
    }
}
