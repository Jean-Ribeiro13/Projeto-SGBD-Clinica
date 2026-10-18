package br.edu.clinica.controller;

import br.edu.clinica.dto.ResumoPrescricoesFiltro;
import br.edu.clinica.model.Medicamento;
import br.edu.clinica.repository.MedicamentoRepository;
import br.edu.clinica.repository.ResumoPrescricoesRepository;
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
@RequestMapping("/prescricoes/resumo")
public class ResumoPrescricoesController {

    private final ResumoPrescricoesRepository repository;
    private final MedicamentoRepository medicamentoRepository;

    public ResumoPrescricoesController(
            ResumoPrescricoesRepository repository,
            MedicamentoRepository medicamentoRepository) {
        this.repository = repository;
        this.medicamentoRepository = medicamentoRepository;
    }

    @GetMapping
    public String abrir(Model model) {
        ResumoPrescricoesFiltro filtro = new ResumoPrescricoesFiltro();
        filtro.setDataInicial(LocalDate.now().withDayOfMonth(1));
        filtro.setDataFinal(LocalDate.now());

        model.addAttribute("filtro", filtro);
        prepararPagina(model);

        return "resumo-prescricoes";
    }

    @GetMapping("/pesquisar")
    public String pesquisar(
            @Valid @ModelAttribute("filtro") ResumoPrescricoesFiltro filtro,
            BindingResult resultado,
            Model model) {

        List<Medicamento> medicamentos = prepararPagina(model);

        if (!resultado.hasFieldErrors("dataInicial")
                && !resultado.hasFieldErrors("dataFinal")
                && filtro.getDataFinal().isBefore(filtro.getDataInicial())) {

            resultado.rejectValue(
                    "dataFinal",
                    "periodo.invalido",
                    "A data final deve ser igual ou posterior à data inicial."
            );
        }

        if (!resultado.hasFieldErrors("idMedicamento")
                && filtro.getIdMedicamento() != null) {

            boolean existe = medicamentos.stream()
                    .anyMatch(medicamento ->
                            filtro.getIdMedicamento()
                                    .equals(medicamento.getIdMedicamento()));

            if (!existe) {
                resultado.rejectValue(
                        "idMedicamento",
                        "medicamento.invalido",
                        "Selecione um medicamento existente."
                );
            }
        }

        if (resultado.hasErrors()) {
            return "resumo-prescricoes";
        }

        model.addAttribute(
                "resumos",
                repository.consultar(
                        filtro.getIdMedicamento(),
                        filtro.getDataInicial(),
                        filtro.getDataFinal()
                )
        );
        model.addAttribute("pesquisado", true);

        return "resumo-prescricoes";
    }

    private List<Medicamento> prepararPagina(Model model) {
        List<Medicamento> medicamentos = medicamentoRepository.listarTodos();

        model.addAttribute("medicamentos", medicamentos);
        model.addAttribute("resumos", List.of());
        model.addAttribute("pesquisado", false);

        return medicamentos;
    }
}
