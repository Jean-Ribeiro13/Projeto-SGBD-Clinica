package br.edu.clinica.controller;

import br.edu.clinica.dto.MedicamentoFormulario;
import br.edu.clinica.model.Medicamento;
import br.edu.clinica.repository.MedicamentoRepository;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;

@Controller
@Profile("mysql")
public class MedicamentoController {

    private final MedicamentoRepository repository;

    public MedicamentoController(MedicamentoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/medicamentos")
    public String listar(Model model) {
        carregarLista(model);
        model.addAttribute("cadastroFormulario", new MedicamentoFormulario());
        return "medicamentos/lista";
    }

    @PostMapping("/medicamentos")
    public String cadastrar(@Valid @ModelAttribute("cadastroFormulario") MedicamentoFormulario formulario,
                            BindingResult resultado, Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            carregarLista(model);
            return "medicamentos/lista";
        }
        repository.cadastrar(formulario.getNome().trim());
        flash.addFlashAttribute("sucesso", "Medicamento cadastrado com sucesso.");
        return "redirect:/medicamentos";
    }

    @GetMapping("/medicamentos/{id}/editar")
    public String editar(@PathVariable int id, Model model) {
        Medicamento medicamento = obter(id);
        MedicamentoFormulario formulario = new MedicamentoFormulario();
        formulario.setNome(medicamento.getNome());
        carregarLista(model);
        model.addAttribute("edicaoFormulario", formulario);
        model.addAttribute("idEdicao", id);
        return "medicamentos/lista";
    }

    @PostMapping("/medicamentos/{id}/editar")
    public String atualizar(@PathVariable int id,
                            @Valid @ModelAttribute("edicaoFormulario") MedicamentoFormulario formulario,
                            BindingResult resultado, Model model, RedirectAttributes flash) {
        obter(id);
        if (resultado.hasErrors()) {
            carregarLista(model);
            model.addAttribute("idEdicao", id);
            return "medicamentos/lista";
        }
        int alterados = repository.atualizar(id, formulario.getNome().trim());
        flash.addFlashAttribute(alterados == 0 ? "erro" : "sucesso",
                alterados == 0 ? "Medicamento não encontrado." : "Medicamento atualizado com sucesso.");
        return "redirect:/medicamentos";
    }

    @PostMapping("/medicamentos/{id}/excluir")
    public String excluir(@PathVariable int id, RedirectAttributes flash) {
        try {
            int removidos = repository.excluir(id);
            flash.addFlashAttribute(removidos == 0 ? "erro" : "sucesso",
                    removidos == 0 ? "Medicamento não encontrado." : "Medicamento excluído com sucesso.");
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("erro",
                    "Não foi possível excluir: este medicamento está vinculado a uma ou mais prescrições.");
        }
        return "redirect:/medicamentos";
    }

    private void carregarLista(Model model) {
        model.addAttribute("medicamentos", repository.listarTodos());
        if (!model.containsAttribute("cadastroFormulario")) {
            model.addAttribute("cadastroFormulario", new MedicamentoFormulario());
        }
    }

    private Medicamento obter(int id) {
        return repository.buscar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicamento não encontrado."));
    }
}
