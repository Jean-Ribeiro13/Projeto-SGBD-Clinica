package br.edu.clinica.controller;

import br.edu.clinica.dto.AgendamentoFormulario;
import br.edu.clinica.repository.AgendamentoRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Profile("mysql")
public class AgendamentoController {

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AgendamentoRepository repository;
    private final BigDecimal valorPadrao;

    // Consulta.valor é obrigatório no banco. Para mudar, defina em application.properties:
    // clinica.agendamento.valor-padrao=150.00
    public AgendamentoController(AgendamentoRepository repository,
                                 @Value("${clinica.agendamento.valor-padrao:0.00}") BigDecimal valorPadrao) {
        this.repository = repository;
        this.valorPadrao = valorPadrao;
    }

    /** Passo 1: o paciente escolhe especialidade, médico e data; o servidor lista os horários livres. */
    @GetMapping("/agendar")
    public String formulario(@RequestParam(required = false) Integer especialidade,
                             @RequestParam(required = false) Integer medico,
                             @RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                             Model model) {
        AgendamentoFormulario formulario = new AgendamentoFormulario();
        formulario.setIdMedico(medico);
        formulario.setData(data);
        model.addAttribute("formulario", formulario);
        prepararTela(model, especialidade, medico, data);
        return "agendamento";
    }

    /** Passo 2: identifica o paciente e grava a consulta. */
    @PostMapping("/agendar")
    public String agendar(@Valid @ModelAttribute("formulario") AgendamentoFormulario formulario,
                          BindingResult resultado, Model model, RedirectAttributes flash) {

        if (!resultado.hasErrors()) {
            var idPaciente = repository.buscarPaciente(formulario.getCpf(), formulario.getDataNascimento());

            if (idPaciente.isEmpty()) {
                // Mensagem única: não revela se foi o CPF ou a data de nascimento que não conferiu.
                resultado.reject("paciente.nao.encontrado",
                        "Não encontramos um paciente com esse CPF e data de nascimento. "
                                + "Procure a recepção para fazer o cadastro.");
            } else {
                LocalTime hora = LocalTime.parse(formulario.getHora());
                boolean livre = repository.horariosLivres(formulario.getIdMedico(), formulario.getData())
                        .contains(hora);

                if (!livre) {
                    resultado.rejectValue("hora", "indisponivel",
                            "Esse horário não está mais disponível. Escolha outro.");
                } else {
                    try {
                        repository.agendar(idPaciente.get(), formulario.getIdMedico(),
                                LocalDateTime.of(formulario.getData(), hora), valorPadrao);

                        String medico = repository.buscarNomeMedico(formulario.getIdMedico()).orElse("o médico");
                        flash.addFlashAttribute("sucesso", "Consulta agendada com " + medico + " em "
                                + formulario.getData().format(DATA_BR) + " às " + hora + ".");
                        return "redirect:/agendar";
                    } catch (DuplicateKeyException e) {
                        // Outra pessoa pegou o mesmo horário entre a busca e o envio (RN16).
                        resultado.rejectValue("hora", "indisponivel",
                                "Esse horário acabou de ser reservado. Escolha outro.");
                    }
                }
            }
        }

        prepararTela(model, null, formulario.getIdMedico(), formulario.getData());
        return "agendamento";
    }

    private void prepararTela(Model model, Integer especialidade, Integer medico, LocalDate data) {
        boolean buscou = medico != null && data != null;

        model.addAttribute("titulo", "Agendar consulta");
        model.addAttribute("especialidades", repository.listarEspecialidades());
        model.addAttribute("medicos", repository.listarMedicos(especialidade));
        model.addAttribute("especialidadeSel", especialidade);
        model.addAttribute("medicoSel", medico);
        model.addAttribute("dataSel", data);
        model.addAttribute("hoje", LocalDate.now());
        model.addAttribute("buscou", buscou);
        model.addAttribute("horarios", buscou
                ? repository.horariosLivres(medico, data).stream().map(LocalTime::toString).toList()
                : List.of());
    }

    @ExceptionHandler(DataAccessException.class)
    public String erroBanco(Model model) {
        model.addAttribute("erro", "Não foi possível acessar a agenda agora. Tente novamente em instantes.");
        model.addAttribute("titulo", "Agendar consulta");
        model.addAttribute("formulario", new AgendamentoFormulario());
        model.addAttribute("especialidades", List.of());
        model.addAttribute("medicos", List.of());
        model.addAttribute("horarios", List.of());
        model.addAttribute("buscou", false);
        model.addAttribute("hoje", LocalDate.now());
        return "agendamento";
    }
}
