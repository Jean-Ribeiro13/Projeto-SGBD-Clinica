package br.edu.clinica.controller;

import br.edu.clinica.dto.ProfissionalFormulario;
import br.edu.clinica.model.ProfissionalSaude;
import br.edu.clinica.repository.ProfissionalSaudeRepository;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;

@Controller
@Profile("mysql")
public class ProfissionalSaudeController {
    private final ProfissionalSaudeRepository repository;

    public ProfissionalSaudeController(ProfissionalSaudeRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/profissionais")
    public String listar(Model model) {
        model.addAttribute("profissionais", repository.listarTodos());
        model.addAttribute("titulo", "Profissionais de Saúde");
        model.addAttribute("tipo", "profissional");
        return "profissionais";
    }

    @GetMapping("/profissionais/novo")
    public String novoProfissional(Model model) {
        model.addAttribute("formulario", new ProfissionalFormulario());
        model.addAttribute("tipo", "profissional");
        model.addAttribute("titulo", "Profissional de Saúde");
        return "profissional-formulario";
    }

    @PostMapping("/profissionais")
    public String criarProfissional(@Valid @ModelAttribute("formulario") ProfissionalFormulario formulario, BindingResult resultado, Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            model.addAttribute("tipo", "profissional");
            model.addAttribute("titulo", "Profissional de Saúde");
            return "profissional-formulario";
        }
        try {
            repository.criar(formulario, "profissional");
            flash.addFlashAttribute("sucesso", "Profissional cadastrado com sucesso.");
        } catch (DuplicateKeyException e) {
            model.addAttribute("tipo", "profissional");
            model.addAttribute("titulo", "Profissional de Saúde");
            model.addAttribute("erro", "CPF já cadastrado.");

            return "profissional-formulario";
        }
        return "redirect:/profissionais";
    }

    @GetMapping("/profissionais/{id}/editar")
    public String editarProfissional(@PathVariable Integer id, Model model) {
        ProfissionalSaude p = obter(id);
        ProfissionalFormulario f = new ProfissionalFormulario();
        f.setNome(p.nome());
        f.setCpf(p.cpf());
        f.setDataNascimento(p.dataNascimento());

        model.addAttribute("formulario", f);
        model.addAttribute("tipo", "profissional");
        model.addAttribute("titulo", "Profissional de Saúde");
        model.addAttribute("id", id);
        return "profissional-formulario";
    }

    @PostMapping("/profissionais/{id}/editar")
    public String atualizarProfissional(@PathVariable Integer id, @Valid @ModelAttribute("formulario") ProfissionalFormulario formulario, BindingResult resultado, Model model, RedirectAttributes flash) {
        obter(id);
        if (resultado.hasErrors()) {
            model.addAttribute("tipo", "profissional");
            model.addAttribute("titulo", "Profissional de Saúde");
            model.addAttribute("id", id);
            return "profissional-formulario";
        }
        try {
            repository.atualizar(id, formulario, "profissional");
            flash.addFlashAttribute("sucesso", "Profissional atualizado com sucesso.");
        } catch (DuplicateKeyException e) {
            model.addAttribute("tipo", "profissional");
            model.addAttribute("titulo", "Profissional de Saúde");
            model.addAttribute("id", id);
            model.addAttribute("erro", "CPF já cadastrado.");

            return "profissional-formulario";
        }
        return "redirect:/profissionais";
    }

    @GetMapping("/medicos")
    public String medicos(Model model) {
        model.addAttribute("profissionais", repository.listarMedicos());
        model.addAttribute("titulo", "Médicos");
        model.addAttribute("tipo", "medico");
        return "profissionais";
    }

    @GetMapping("/enfermeiros")
    public String enfermeiros(Model model) {
        model.addAttribute("profissionais", repository.listarEnfermeiros());
        model.addAttribute("titulo", "Enfermeiros");
        model.addAttribute("tipo", "enfermeiro");
        return "profissionais";
    }

    @GetMapping("/{tipo:medico|enfermeiro}/novo")
    public String novo(@PathVariable String tipo, Model model) {
        validarTipo(tipo);
        prepararFormulario(model, tipo, new ProfissionalFormulario());
        return "profissional-formulario";
    }

    @PostMapping("/{tipo:medico|enfermeiro}")
    public String criar(@PathVariable String tipo, @Valid @ModelAttribute("formulario") ProfissionalFormulario formulario,
                        BindingResult resultado, Model model, RedirectAttributes flash) {
        validarTipo(tipo);
        validarRegistro(tipo, formulario, resultado);
        if ("medico".equals(tipo) && formulario.getSupervisorId() != null && !repository.existeMedico(formulario.getSupervisorId()))
            resultado.rejectValue("supervisorId", "invalido", "Selecione um médico supervisor existente.");
        if (resultado.hasErrors()) {
            prepararFormulario(model, tipo, formulario);
            return "profissional-formulario";
        }
        try {
            repository.criar(formulario, tipo);
            flash.addFlashAttribute("sucesso", titulo(tipo) + " cadastrado com sucesso.");
        } catch (DuplicateKeyException e) {
            prepararFormulario(model, tipo, formulario);

            model.addAttribute(
                    "erro",
                    "CPF ou registro de profissional já cadastrado"
            );

            return "profissional-formulario";
        }
        return "redirect:/" + tipo + "s";
    }

    @GetMapping("/{tipo:medico|enfermeiro}/{id}/editar")
    public String editar(@PathVariable String tipo, @PathVariable Integer id, Model model) {
        validarTipo(tipo);
        ProfissionalSaude p = obter(id);
        if ("medico".equals(tipo) ? !repository.existeMedico(id) : !repository.existeEnfermeiro(id))
            throw naoEncontrado();
        ProfissionalFormulario f = new ProfissionalFormulario();
        f.setNome(p.nome());
        f.setCpf(p.cpf());
        f.setDataNascimento(p.dataNascimento());
        f.setRegistro(p.registro());
        f.setSupervisorId(p.supervisorId());
        prepararFormulario(model, tipo, f);
        model.addAttribute("id", id);
        return "profissional-formulario";
    }

    @PostMapping("/{tipo:medico|enfermeiro}/{id}/editar")
    public String atualizar(@PathVariable String tipo, @PathVariable Integer id,
                            @Valid @ModelAttribute("formulario") ProfissionalFormulario formulario, BindingResult resultado,
                            Model model, RedirectAttributes flash) {
        validarTipo(tipo);
        obter(id);
        boolean especializacaoExiste = "medico".equals(tipo)
                ? repository.existeMedico(id)
                : repository.existeEnfermeiro(id);

        if (!especializacaoExiste) {
            throw naoEncontrado();
        }
        validarRegistro(tipo, formulario, resultado);
        if ("medico".equals(tipo) && formulario.getSupervisorId() != null && (!repository.existeMedico(formulario.getSupervisorId()) || formulario.getSupervisorId().equals(id)))
            resultado.rejectValue("supervisorId", "invalido", "Selecione outro médico supervisor válido.");
        if (resultado.hasErrors()) {
            prepararFormulario(model, tipo, formulario);
            model.addAttribute("id", id);
            if ("medico".equals(tipo)) {
                model.addAttribute(
                        "supervisores",
                        repository.listarMedicosSem(id)
                );
            }
            return "profissional-formulario";
        }
        try {
            repository.atualizar(id, formulario, tipo);
            flash.addFlashAttribute("sucesso", titulo(tipo) + " atualizado com sucesso.");
        } catch (DuplicateKeyException e) {
            prepararFormulario(model, tipo, formulario);
            model.addAttribute("id", id);

            model.addAttribute(
                    "erro",
                    "CPF ou registro profissional já cadastrado."
            );

            return "profissional-formulario";
        }
        return "redirect:/" + tipo + "s";
    }

    @PostMapping("/{tipo:medico|enfermeiro}/{id}/excluir")
    public String excluir(@PathVariable String tipo, @PathVariable Integer id, RedirectAttributes flash) {
        validarTipo(tipo);
        try {
            int n = repository.excluirEspecializacao(id, tipo);
            flash.addFlashAttribute(n == 0 ? "erro" : "sucesso", n == 0 ? "Registro não encontrado." : titulo(tipo) + " removido. O profissional de saúde foi mantido.");
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("erro", "Não foi possível remover: existem registros vinculados a este profissional.");
        }
        return "redirect:/" + tipo + "s";
    }

    @PostMapping("/profissionais/{id}/excluir")
    public String excluirProfissional(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            int n = repository.excluirProfissional(id);
            flash.addFlashAttribute(n == 0 ? "erro" : "sucesso", n == 0 ? "Profissional não encontrado." : "Profissional excluído.");
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("erro", "Não foi possível excluir: há consultas ou evoluções clínicas vinculadas.");
        }
        return "redirect:/profissionais";
    }

    @ExceptionHandler(DataAccessException.class)
    public String erroBanco(DataAccessException e, Model model) {
        model.addAttribute("erro", "Não foi possível acessar os profissionais no banco de dados.");
        model.addAttribute("profissionais", java.util.List.of());
        return "profissionais";
    }

    private void prepararFormulario(Model model, String tipo, ProfissionalFormulario f) {
        model.addAttribute("tipo", tipo);
        model.addAttribute("titulo", titulo(tipo));
        model.addAttribute("formulario", f);
        if ("medico".equals(tipo)) model.addAttribute("supervisores", repository.listarMedicosSem(null));
    }

    private ProfissionalSaude obter(Integer id) {
        return repository.buscar(id).orElseThrow(ProfissionalSaudeController::naoEncontrado);
    }

    private static ResponseStatusException naoEncontrado() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado.");
    }

    private static String titulo(String tipo) {
        return "medico".equals(tipo) ? "Médico" : "Enfermeiro";
    }

    private static void validarTipo(String tipo) {
        if (!"medico".equals(tipo) && !"enfermeiro".equals(tipo))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private static void validarRegistro(String tipo, ProfissionalFormulario f, BindingResult resultado) {
        if (f.getRegistro() == null || f.getRegistro().isBlank())
            resultado.rejectValue("registro", "obrigatorio", "Informe o CRM ou COREN.");
    }
}
