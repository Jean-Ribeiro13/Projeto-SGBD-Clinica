package br.edu.clinica.controller;

import br.edu.clinica.dto.ResumoConsultasFiltro;
import br.edu.clinica.repository.ProfissionalSaudeRepository;
import br.edu.clinica.repository.RelatorioConsolidadoRepository;
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
@RequestMapping("/relatorios/consolidado")
public class RelatorioConsolidadoController {

    private final RelatorioConsolidadoRepository relatorioRepository;
    private final ProfissionalSaudeRepository profissionalRepository;

    public RelatorioConsolidadoController(
            RelatorioConsolidadoRepository relatorioRepository,
            ProfissionalSaudeRepository profissionalRepository) {
        this.relatorioRepository = relatorioRepository;
        this.profissionalRepository = profissionalRepository;
    }

    @GetMapping
    public String abrir(Model model) {
        ResumoConsultasFiltro filtro = new ResumoConsultasFiltro();
        filtro.setDataInicial(LocalDate.now().withDayOfMonth(1));
        filtro.setDataFinal(LocalDate.now());
        prepararPagina(model, filtro);
        return "relatorio-consolidado";
    }

    @GetMapping("/pesquisar")
    public String pesquisar(
            @Valid @ModelAttribute("filtro") ResumoConsultasFiltro filtro,
            BindingResult resultado,
            Model model) {

        prepararPagina(model, filtro);

        if (!resultado.hasFieldErrors("dataInicial")
                && !resultado.hasFieldErrors("dataFinal")
                && filtro.getDataFinal().isBefore(filtro.getDataInicial())) {
            resultado.rejectValue("dataFinal", "periodo.invalido",
                    "A data final deve ser igual ou posterior à data inicial.");
        }

        if (!resultado.hasFieldErrors("idMedico")
                && filtro.getIdMedico() != null
                && !profissionalRepository.existeMedico(filtro.getIdMedico())) {
            resultado.rejectValue("idMedico", "medico.invalido", "Selecione um médico existente.");
        }

        if (resultado.hasErrors()) {
            return "relatorio-consolidado";
        }

        model.addAttribute("resumos", relatorioRepository.consultar(
                filtro.getIdMedico(), filtro.getDataInicial(), filtro.getDataFinal()));
        model.addAttribute("pesquisado", true);
        return "relatorio-consolidado";
    }

    private void prepararPagina(Model model, ResumoConsultasFiltro filtro) {
        model.addAttribute("filtro", filtro);
        model.addAttribute("medicos", profissionalRepository.listarMedicos());
        model.addAttribute("resumos", List.of());
        model.addAttribute("pesquisado", false);
    }
}
