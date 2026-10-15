package br.edu.clinica.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("mysql")
public class BancoController {
    private final JdbcTemplate jdbc;

    public BancoController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/teste-banco")
    public String testarConexao() {
        // Apenas testa a conexao: nao cria tabelas nem altera registros.
        Integer resultado = jdbc.queryForObject("SELECT 1", Integer.class);
        if (!Integer.valueOf(1).equals(resultado)) {
            throw new IllegalStateException("Resposta inesperada do banco.");
        }
        return "Conexão com o MySQL funcionando!";
    }
}
