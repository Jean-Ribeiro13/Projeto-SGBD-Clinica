package br.edu.clinica.controller;

import br.edu.clinica.model.Medicamento;
import br.edu.clinica.repository.MedicamentoRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Profile("mysql")
@RequestMapping("/medicamentos")
public class MedicamentoController {

    private final MedicamentoRepository repository;

    public MedicamentoController(MedicamentoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("medicamentos", repository.listarTodos());
        return "medicamentos/lista";
    }

    @PostMapping
    public String cadastrar(@RequestParam String nome) {
        repository.cadastrar(nome);
        return "redirect:/medicamentos";
    }

    @PostMapping("/{id}/editar")
    public String editar(@PathVariable int id,
                         @RequestParam String nome) {
        repository.atualizar(id, nome);
        return "redirect:/medicamentos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable int id) {
        repository.excluir(id);
        return "redirect:/medicamentos";
    }
}
