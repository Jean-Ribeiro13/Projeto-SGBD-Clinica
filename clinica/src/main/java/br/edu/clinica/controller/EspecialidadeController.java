package br.edu.clinica.controller;

import br.edu.clinica.dto.EspecialidadeFormulario;
import br.edu.clinica.model.Especialidade;
import br.edu.clinica.repository.EspecialidadeRepository;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Profile("mysql")
public class EspecialidadeController {
    private static final String MSG_DUPLICADA = "Já existe uma especialidade com esse nome.";
    private final EspecialidadeRepository repository;

    public EspecialidadeController(EspecialidadeRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/especialidades")
    public String listar(Model model) {
        model.addAttribute("especialidades", repository.listarTodas());
        model.addAttribute("titulo", "Especialidades");
        return "especialidades";
    }

    @GetMapping("/especialidades/novo")
    public String novo(Model model) {
        model.addAttribute("formulario", new EspecialidadeFormulario());
        model.addAttribute("titulo", "Especialidade");
        return "especialidade-formulario";
    }

    @PostMapping("/especialidades")
    public String criar(@Valid @ModelAttribute("formulario") EspecialidadeFormulario formulario,
                        BindingResult resultado, Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            model.addAttribute("titulo", "Especialidade");
            return "especialidade-formulario";
        }
        try {
            repository.criar(formulario.getNome().trim());
            flash.addFlashAttribute("sucesso", "Especialidade cadastrada com sucesso.");
        } catch (DuplicateKeyException e) {
            resultado.rejectValue("nome", "duplicado", MSG_DUPLICADA);
            model.addAttribute("titulo", "Especialidade");
            return "especialidade-formulario";
        }
        return "redirect:/especialidades";
    }

    @GetMapping("/especialidades/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        Especialidade e = obter(id);
        EspecialidadeFormulario f = new EspecialidadeFormulario();
        f.setNome(e.nome());
        model.addAttribute("formulario", f);
        model.addAttribute("titulo", "Especialidade");
        model.addAttribute("id", id);
        return "especialidade-formulario";
    }

    @PostMapping("/especialidades/{id}/editar")
    public String atualizar(@PathVariable Integer id,
                            @Valid @ModelAttribute("formulario") EspecialidadeFormulario formulario,
                            BindingResult resultado, Model model, RedirectAttributes flash) {
        obter(id);
        if (resultado.hasErrors()) {
            model.addAttribute("titulo", "Especialidade");
            model.addAttribute("id", id);
            return "especialidade-formulario";
        }
        try {
            repository.atualizar(id, formulario.getNome().trim());
            flash.addFlashAttribute("sucesso", "Especialidade atualizada com sucesso.");
        } catch (DuplicateKeyException e) {
            resultado.rejectValue("nome", "duplicado", MSG_DUPLICADA);
            model.addAttribute("titulo", "Especialidade");
            model.addAttribute("id", id);
            return "especialidade-formulario";
        }
        return "redirect:/especialidades";
    }

    @PostMapping("/especialidades/{id}/excluir")
    public String excluir(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            int n = repository.excluir(id);
            flash.addFlashAttribute(n == 0 ? "erro" : "sucesso",
                    n == 0 ? "Especialidade não encontrada." : "Especialidade excluída com sucesso.");
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("erro", "Não foi possível excluir: existem médicos vinculados a esta especialidade.");
        }
        return "redirect:/especialidades";
    }

    @ExceptionHandler(DataAccessException.class)
    public String erroBanco(DataAccessException e, Model model) {
        model.addAttribute("erro", "Não foi possível acessar as especialidades no banco de dados.");
        model.addAttribute("especialidades", java.util.List.of());
        model.addAttribute("titulo", "Especialidades");
        return "especialidades";
    }

    private Especialidade obter(Integer id) {
        return repository.buscar(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Especialidade não encontrada."));
    }
}