package br.edu.clinica.controller;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Profile("mysql")
public class RecepcaoController {
    private final JdbcTemplate jdbc;

    public RecepcaoController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/recepcao/pacientes")
    public String pacientes(Model model) {
        String sql = """
                SELECT id_paciente, nome, cpf_mascarado, data_nascimento
                FROM vw_paciente_recepcao
                ORDER BY nome
                """;
        List<PacienteRecepcao> pacientes = jdbc.query(sql, (rs, row) -> new PacienteRecepcao(
                rs.getInt("id_paciente"), rs.getString("nome"),
                rs.getString("cpf_mascarado"), rs.getString("data_nascimento")));
        model.addAttribute("pacientes", pacientes);
        return "recepcao/pacientes";
    }

    public record PacienteRecepcao(int id, String nome, String cpfMascarado, String dataNascimento) {
    }
}
