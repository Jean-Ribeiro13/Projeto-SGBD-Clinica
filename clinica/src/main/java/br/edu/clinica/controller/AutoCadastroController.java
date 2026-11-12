package br.edu.clinica.controller;

import br.edu.clinica.dto.AutoCadastroFormulario;
import br.edu.clinica.dto.PacienteFormulario;
import br.edu.clinica.repository.PacienteRepository;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Página pública em que o paciente se cadastra sozinho e segue para o agendamento. */
@Controller
@Profile("mysql")
public class AutoCadastroController {

    private final PacienteRepository pacientes;

    public AutoCadastroController(PacienteRepository pacientes) {
        this.pacientes = pacientes;
    }

    @GetMapping("/cadastrar")
    public String formulario(Model model) {
        model.addAttribute("formulario", new AutoCadastroFormulario());
        return "autocadastro";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@Valid @ModelAttribute("formulario") AutoCadastroFormulario formulario,
                            BindingResult resultado, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return "autocadastro";
        }

        PacienteFormulario paciente = new PacienteFormulario();
        paciente.setNome(formulario.getNome().trim());
        paciente.setCpf(formulario.getCpf());
        paciente.setDataNascimento(formulario.getDataNascimento());

        try {
            pacientes.cadastrar(paciente);
        } catch (DuplicateKeyException e) {
            // uq_paciente_cpf: o CPF já existe no banco.
            resultado.rejectValue("cpf", "duplicado",
                    "Esse CPF já está cadastrado. Vá direto ao agendamento ou procure a recepção.");
            return "autocadastro";
        }

        flash.addFlashAttribute("sucesso", "Cadastro realizado. Agora é só agendar sua consulta.");
        return "redirect:/agendar";
    }

    @ExceptionHandler(DataAccessException.class)
    public String erroBanco(Model model) {
        model.addAttribute("erro", "Não foi possível concluir o cadastro agora. Tente novamente em instantes.");
        model.addAttribute("formulario", new AutoCadastroFormulario());
        return "autocadastro";
    }
}
