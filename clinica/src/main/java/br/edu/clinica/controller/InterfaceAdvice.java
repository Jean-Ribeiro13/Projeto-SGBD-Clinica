package br.edu.clinica.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class InterfaceAdvice {

    @ModelAttribute
    public void prepararInterface(
            Model model,
            Authentication authentication,
            HttpServletRequest request) {

        boolean autenticado = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        boolean admin = temPerfil(authentication, "ADMIN");
        boolean medico = temPerfil(authentication, "MEDICO");
        boolean recepcao = temPerfil(authentication, "RECEPCAO");

        model.addAttribute("uiAutenticado", autenticado);
        model.addAttribute("uiAdmin", admin);
        model.addAttribute("uiClinico", admin || medico);
        model.addAttribute("uiRecepcao", admin || recepcao);

        model.addAttribute(
                "uiNome",
                autenticado ? authentication.getName() : "Visitante"
        );

        model.addAttribute(
                "uiPerfil",
                admin ? "Administração"
                        : medico ? "Médico"
                        : recepcao ? "Recepção"
                        : "Visitante"
        );

        String caminho = request.getRequestURI()
                .substring(request.getContextPath().length());

        model.addAttribute("uiCaminho", caminho);
    }

    private boolean temPerfil(
            Authentication authentication,
            String perfil) {

        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_" + perfil));
    }
}
