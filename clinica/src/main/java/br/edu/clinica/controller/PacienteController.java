package br.edu.clinica.controller;

import br.edu.clinica.repository.PacienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import br.edu.clinica.dto.PacienteFormulario;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.dao.DuplicateKeyException;

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
            @ModelAttribute PacienteFormulario formulario){ // Instrui o Spring a preencher 'PacienteFormulario' com os campos enviados, associando os seus nomes aos atributos
            repository.cadastrar(formulario); // Entrega os dados recebidos ao repository, que executa o INSERT no MySQL.
        return "redirect:/pacientes"; // Manda o navegador abrir a listagem após a gravação. A listagem consulta novamente o banco, incluindo o novo paciente
    }


    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String tratarErroBanco(DataAccessException erro, Model model) {
        log.error("Falha ao consultar a lista de pacientes.", erro);
        model.addAttribute("erro", "Não foi possível carregar os pacientes. Tente novamente em instantes.");
        return "pacientes";
    }
}
