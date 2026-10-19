package br.edu.clinica.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Profile("mysql")
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
