package br.edu.clinica.controller;

import br.edu.clinica.dto.BuscaConsultasFormulario;
import br.edu.clinica.model.ConsultaPaciente;
import br.edu.clinica.repository.AgendamentoRepository;
import br.edu.clinica.repository.MinhasConsultasRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/** Página pública em que o paciente consulta os próprios agendamentos. */
@Controller
@Profile("mysql")
public class MinhasConsultasController {

    private final AgendamentoRepository pacientes;
    private final MinhasConsultasRepository consultas;

    public MinhasConsultasController(AgendamentoRepository pacientes, MinhasConsultasRepository consultas) {
        this.pacientes = pacientes;
        this.consultas = consultas;
    }

    @GetMapping("/minhas-consultas")
    public String formulario(Model model) {
        model.addAttribute("formulario", new BuscaConsultasFormulario());
        return "minhas-consultas";
    }

    // POST (e não GET) para o CPF não aparecer na URL, no histórico do navegador nem em logs.
    @PostMapping("/minhas-consultas")
    public String buscar(@Valid @ModelAttribute("formulario") BuscaConsultasFormulario formulario,
                         BindingResult resultado, Model model) {
        if (resultado.hasErrors()) {
            return "minhas-consultas";
        }

        var idPaciente = pacientes.buscarPaciente(formulario.getCpf(), formulario.getDataNascimento());
        if (idPaciente.isEmpty()) {
            // Mensagem única: não revela se foi o CPF ou a data de nascimento que não conferiu.
            resultado.reject("paciente.nao.encontrado",
                    "Não encontramos um paciente com esse CPF e data de nascimento.");
            return "minhas-consultas";
        }

        List<ConsultaPaciente> todas = consultas.listarPorPaciente(idPaciente.get());
        LocalDateTime agora = LocalDateTime.now();

        model.addAttribute("buscou", true);
        model.addAttribute("proximas", todas.stream()
                .filter(c -> !c.dataHora().isBefore(agora))
                .toList());
        model.addAttribute("anteriores", todas.stream()
                .filter(c -> c.dataHora().isBefore(agora))
                .sorted(Comparator.comparing(ConsultaPaciente::dataHora).reversed())
                .toList());
        return "minhas-consultas";
    }

    @ExceptionHandler(DataAccessException.class)
    public String erroBanco(Model model) {
        model.addAttribute("erro", "Não foi possível consultar a agenda agora. Tente novamente em instantes.");
        model.addAttribute("formulario", new BuscaConsultasFormulario());
        return "minhas-consultas";
    }
}
