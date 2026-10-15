package br.edu.clinica.controller;

import br.edu.clinica.model.Paciente;
import br.edu.clinica.repository.PacienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import br.edu.clinica.dto.PacienteFormulario;
import org.springframework.dao.DuplicateKeyException;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
    Executa o SELECT no MySQL e transforma as linhas em objetos
 */

@Controller
@Profile("mysql")
public class PacienteController {
    private static final Logger log = LoggerFactory.getLogger(PacienteController.class);
    private final PacienteRepository repository;

    public PacienteController(PacienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/pacientes")
    public String listar(Model model) {
        model.addAttribute("pacientes", repository.listarTodos());
        return "pacientes";
    }

    @GetMapping("/pacientes/novo")
    public String novo(Model model){
        model.addAttribute("formulario", new PacienteFormulario());
        return "paciente-formulario";
    }

    @PostMapping("/pacientes") // Recebe o envio feito pelo formulario
    public String cadastrar(
            @Valid // @Valid solicita a validação do objeto recebido.
            @ModelAttribute("formulario") PacienteFormulario formulario, // Dá ao objeto recebido o mesmo nome usado no th:object do HTML. Assim, a página consegue recuperar os valores.
            BindingResult resultado, // BindingResult reúne os erros encontrados e deve ficar imediatamente depois do parâmetro validado.
            Model model,
            RedirectAttributes redirectAttributes) {

            if (resultado.hasErrors()) { // hasErrors() verifica se houve erro. Se houver, mostramos novamente o formulário antes de chamar o repository.
                return "paciente-formulario";
            }
            try { // Executa a tentativa de cadastro
                repository.cadastrar(formulario);// Entrega os dados recebidos ao repository, que executa o INSERT no MySQL.

                redirectAttributes.addFlashAttribute(
                        "sucesso",
                        "Paciente cadastrado com sucesso!"
                );
                return "redirect:/pacientes"; // Manda o navegador abrir a listagem após a gravação. A listagem consulta novamente o banco, incluindo o novo paciente
            } catch (DuplicateKeyException erro) { // Trata a violação de chave única. Nesse cadastro, o CPF é único e o ID é gerado pelo banco
                model.addAttribute(
                        "erro",
                        "Ja existe um paciente cadastrado com esse CPF."
                );
                return "paciente-formulario"; // No erro retorna sem redirecionar. Isso permite renderizar novamente o formulario com o objeto recebido.
            }
        }

        @GetMapping("/pacientes/{id}/editar")
        public String Editar(@PathVariable("id") Integer id, Model model) {

            Paciente paciente = repository.buscarPorId(id).orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Paciente não encontrado."
                    ));
            PacienteFormulario formulario = new PacienteFormulario();

            formulario.setNome(paciente.getNome());
            formulario.setCpf(paciente.getCpf());
            formulario.setDataNascimento(paciente.getDataNascimento());

            model.addAttribute("formulario", formulario);
            model.addAttribute("idPaciente", id);

            return "paciente-edicao";
        }

    @PostMapping("/pacientes/{id}/editar")
    public String atualizar(
            @PathVariable("id") Integer id,
            @Valid @ModelAttribute("formulario") PacienteFormulario formulario,
            BindingResult resultado,
            Model model,
            RedirectAttributes redirectAttributes) {

        model.addAttribute("idPaciente", id);

        if (resultado.hasErrors()) {
            return "paciente-edicao";
        }
        repository.buscarPorId(id).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Paciente não encontrado."
                ));
        try {
            repository.atualizar(id, formulario);
            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Paciente atualizado com sucesso!"
            );
            return "redirect:/pacientes";

        } catch (DuplicateKeyException erro) {
            model.addAttribute(
                    "erro",
                    "Já existe outro paciente cadastrado com esse CPF."
            );
            return "paciente-edicao";
        }
    }

    @PostMapping("/pacientes/{id}/excluir")
    public String excluir(
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            int quantidade = repository.excluir(id);

            if (quantidade == 0) {
                redirectAttributes.addFlashAttribute(
                        "erro",
                        "Paciente não encontrado. Ele pode já ter sido excluído."
                );
            } else {
                redirectAttributes.addFlashAttribute(
                        "sucesso",
                        "Paciente excluído com sucesso!"
                );
            }
        } catch (DataIntegrityViolationException erro) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Não foi possível excluir o paciente porque existem registros vinculados, como consultas ou prontuário."
            );
        }
        return "redirect:/pacientes";
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String tratarErroBanco(DataAccessException erro, Model model) {
        log.error("Falha ao consultar a lista de pacientes.", erro);
        model.addAttribute("erro", "Não foi possível carregar os pacientes. Tente novamente em instantes.");
        return "pacientes";
    }
}
