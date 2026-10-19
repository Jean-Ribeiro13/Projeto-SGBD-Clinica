package br.edu.clinica.config;

import java.util.List;
import java.util.Locale;

import br.edu.clinica.security.SaltedSha256PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@Profile("mysql")

public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error", "/favicon.ico").permitAll()
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/recepcao/pacientes").hasAnyRole("RECEPCAO", "ADMIN")
                        .requestMatchers("/pacientes", "/pacientes/**")
                        .hasAnyRole("MEDICO", "ADMIN")
                        .requestMatchers("/medicamentos", "/medicamentos/**")
                        .hasAnyRole("MEDICO", "ADMIN")
                        .requestMatchers("/relatorios/**", "/consultas/resumo", "/consultas/resumo/**",
                                "/prescricoes/resumo", "/prescricoes/resumo/**")
                        .hasAnyRole("MEDICO", "ADMIN")
                        .requestMatchers("/profissionais", "/profissionais/**", "/medicos", "/medicos/**",
                                "/enfermeiros", "/enfermeiros/**", "/especialidades", "/especialidades/**",
                                "/medico-especialidades", "/medico-especialidades/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/teste-banco").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", false)
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout"))
                .build();
    }

    @Bean
    UserDetailsService userDetailsService(JdbcTemplate jdbc) {
        return login -> {
            String sql = """
                    SELECT login, senha_hash, salt, perfil, ativo
                    FROM Usuario_App
                    WHERE login = ?
                    """;
            List<UserDetails> encontrados = jdbc.query(sql, (rs, row) -> {
                String perfil = rs.getString("perfil").toUpperCase(Locale.ROOT);
                String credencial = rs.getString("salt") + "$" + rs.getString("senha_hash");
                return User.withUsername(rs.getString("login"))
                        .password(credencial)
                        .authorities("ROLE_" + perfil)
                        .disabled(!rs.getBoolean("ativo"))
                        .build();
            }, login);

            if (encontrados.isEmpty()) {
                throw new UsernameNotFoundException("Usuário não encontrado.");
            }
            return encontrados.get(0);
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new SaltedSha256PasswordEncoder();
    }
}
