package br.edu.clinica.controller;

import br.edu.clinica.dto.ResumoConsultasFiltro;
import br.edu.clinica.repository.ProfissionalSaudeRepository;
import br.edu.clinica.repository.ResumoConsultasRepository;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@Profile("mysql")
@RequestMapping("/consultas/resumo")
public class ResumoConsultasController {

    private final ResumoConsultasRepository repository;
    private final ProfissionalSaudeRepository profissionalRepository;

    public ResumoConsultasController(
            ResumoConsultasRepository repository,
            ProfissionalSaudeRepository profissionalRepository) {
        this.repository = repository;
        this.profissionalRepository = profissionalRepository;
    }

    @GetMapping
    public String abrir(Model model) {
        ResumoConsultasFiltro filtro = new ResumoConsultasFiltro();
        filtro.setDataInicial(LocalDate.now().withDayOfMonth(1));
        filtro.setDataFinal(LocalDate.now());

        model.addAttribute("filtro", filtro);
        prepararPagina(model);
        return "resumo-consultas";
    }

    @GetMapping("/pesquisar")
    public String pesquisar(
            @Valid @ModelAttribute("filtro") ResumoConsultasFiltro filtro,
            BindingResult resultado,
            Model model) {

        prepararPagina(model);

        if (!resultado.hasFieldErrors("dataInicial")
                && !resultado.hasFieldErrors("dataFinal")
                && filtro.getDataFinal().isBefore(filtro.getDataInicial())) {
            resultado.rejectValue(
                    "dataFinal",
                    "periodo.invalido",
                    "A data final deve ser igual ou posterior à data inicial."
            );
        }

        if (!resultado.hasFieldErrors("idMedico")
                && filtro.getIdMedico() != null
                && !profissionalRepository.existeMedico(filtro.getIdMedico())) {
            resultado.rejectValue(
                    "idMedico",
                    "medico.invalido",
                    "Selecione um médico existente."
            );
        }

        if (resultado.hasErrors()) {
            return "resumo-consultas";
        }

        model.addAttribute(
                "resumos",
                repository.consultar(
                        filtro.getIdMedico(),
                        filtro.getDataInicial(),
                        filtro.getDataFinal()
                )
        );
        model.addAttribute("pesquisado", true);

        return "resumo-consultas";
    }

    private void prepararPagina(Model model) {
        model.addAttribute("medicos", profissionalRepository.listarMedicos());
        model.addAttribute("resumos", List.of());
        model.addAttribute("pesquisado", false);
    }
}