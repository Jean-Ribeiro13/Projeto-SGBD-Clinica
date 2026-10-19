package br.edu.clinica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/** Keeps the instructional no-database profile accessible without a database login. */
@Configuration
@Profile("sem-banco")
public class SemBancoSecurityConfig {
    @Bean
    SecurityFilterChain semBancoSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()).build();
    }
}
