package br.edu.clinica.controller;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Profile("mysql")
@RequestMapping("/usuarios")
public class UsuarioAppController {

    private static final Set<String> PERFIS = Set.of("recepcao", "medico", "admin");
    private static final Pattern LOGIN_VALIDO = Pattern.compile("[A-Za-z0-9._-]{3,50}");

    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;

    public UsuarioAppController(JdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/novo")
    public String formulario(Model model) {
        model.addAttribute("perfis", List.of("recepcao", "medico", "admin"));
        return "cadastro-usuario";
    }

    @PostMapping
    public String cadastrar(
            @RequestParam String login,
            @RequestParam String senha,
            @RequestParam String confirmarSenha,
            @RequestParam String perfil,
            RedirectAttributes redirect) {

        String loginNormalizado = login.trim();
        String perfilNormalizado = perfil.trim().toLowerCase();

        if (!LOGIN_VALIDO.matcher(loginNormalizado).matches()) {
            redirect.addFlashAttribute("erro", "Use um login de 3 a 50 caracteres: letras, números, ponto, hífen ou sublinhado.");
            return "redirect:/usuarios/novo";
        }
        if (senha.length() < 8 || senha.length() > 100) {
            redirect.addFlashAttribute("erro", "A senha deve ter entre 8 e 100 caracteres.");
            return "redirect:/usuarios/novo";
        }
        if (!senha.equals(confirmarSenha)) {
            redirect.addFlashAttribute("erro", "As senhas não conferem.");
            return "redirect:/usuarios/novo";
        }
        if (!PERFIS.contains(perfilNormalizado)) {
            redirect.addFlashAttribute("erro", "Selecione um perfil válido.");
            return "redirect:/usuarios/novo";
        }

        try {
            String[] saltEHash = passwordEncoder.encode(senha).split("\\$", 2);
            jdbc.update("""
                    INSERT INTO Usuario_App (login, senha_hash, salt, perfil)
                    VALUES (?, ?, ?, ?)
                    """, loginNormalizado, saltEHash[1], saltEHash[0], perfilNormalizado);
            redirect.addFlashAttribute("sucesso", "Usuário cadastrado. Ele já pode entrar com o login criado.");
        } catch (DuplicateKeyException e) {
            redirect.addFlashAttribute("erro", "Esse login já está cadastrado.");
        } catch (DataAccessException e) {
            redirect.addFlashAttribute("erro", "Não foi possível cadastrar. Verifique se a tabela Usuario_App existe e tente novamente.");
        }
        return "redirect:/usuarios/novo";
    }
}
