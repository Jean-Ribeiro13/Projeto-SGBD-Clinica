package br.edu.clinica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {
    @GetMapping("/")
    public String inicio(Model model, Authentication authentication) {
        // O nome retornado corresponde ao arquivo templates/inicio.html.
        boolean podeGerenciarUsuarios = authentication != null
                && authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("podeGerenciarUsuarios", podeGerenciarUsuarios);
        return "inicio";
    }
}
