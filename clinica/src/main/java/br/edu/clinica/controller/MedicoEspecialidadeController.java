package br.edu.clinica.controller;

import br.edu.clinica.dto.MedicoEspecialidadeFormulario;
import br.edu.clinica.repository.MedicoEspecialidadeRepository;
import br.edu.clinica.repository.ProfissionalSaudeRepository;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@Profile("mysql")
@RequestMapping("/medico-especialidades")
public class MedicoEspecialidadeController {
    private final MedicoEspecialidadeRepository repository;
    private final ProfissionalSaudeRepository profissionalRepository;

    public MedicoEspecialidadeController(MedicoEspecialidadeRepository repository,
                                         ProfissionalSaudeRepository profissionalRepository) {
        this.repository = repository;
        this.profissionalRepository = profissionalRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("vinculos", repository.listarTodos());
        model.addAttribute("formulario", new MedicoEspecialidadeFormulario());
        carregarOpcoes(model);
        return "medico-especialidades";
    }

    @PostMapping
    public String cadastrar(@Valid @ModelAttribute("formulario") MedicoEspecialidadeFormulario formulario,
                            BindingResult resultado, Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            model.addAttribute("vinculos", repository.listarTodos());
            carregarOpcoes(model);
            return "medico-especialidades";
        }

        try {
            repository.cadastrar(formulario.getIdMedico(), formulario.getIdEspecialidade(), formulario.getDataObtencao());
            flash.addFlashAttribute("sucesso", "Especialidade vinculada ao médico.");
        } catch (DuplicateKeyException e) {
            flash.addFlashAttribute("erro", "Este médico já possui essa especialidade cadastrada.");
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("erro", "Selecione um médico e uma especialidade existentes.");
        }
        return "redirect:/medico-especialidades";
    }

    @PostMapping("/{idMedico}/{idEspecialidade}/editar")
    public String atualizarData(@PathVariable Integer idMedico,
                                @PathVariable Integer idEspecialidade,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataObtencao,
                                RedirectAttributes flash) {
        int quantidade = repository.atualizarData(idMedico, idEspecialidade, dataObtencao);
        flash.addFlashAttribute(quantidade == 0 ? "erro" : "sucesso",
                quantidade == 0 ? "Vínculo não encontrado." : "Data de obtenção atualizada.");
        return "redirect:/medico-especialidades";
    }

    @PostMapping("/{idMedico}/{idEspecialidade}/excluir")
    public String excluir(@PathVariable Integer idMedico,
                          @PathVariable Integer idEspecialidade,
                          RedirectAttributes flash) {
        int quantidade = repository.excluir(idMedico, idEspecialidade);
        flash.addFlashAttribute(quantidade == 0 ? "erro" : "sucesso",
                quantidade == 0 ? "Vínculo não encontrado." : "Vínculo médico–especialidade removido.");
        return "redirect:/medico-especialidades";
    }

    private void carregarOpcoes(Model model) {
        model.addAttribute("medicos", profissionalRepository.listarMedicos());
        model.addAttribute("especialidades", repository.listarEspecialidades());
    }
}
