package br.com.edu.fatec.IPEMControl.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * CAUSA 1 — Spring Security bloqueando tudo.
 *
 * Se o projeto tem spring-boot-starter-security no pom.xml,
 * ele ativa autenticação HTTP Basic em TODOS os endpoints por padrão.
 * Isso faz o browser receber 401 em /vehicles, /usuarios/login, etc.
 * — mesmo com @CrossOrigin configurado.
 *
 * Esta classe desabilita completamente a segurança HTTP padrão,
 * liberando todos os endpoints sem autenticação (a autenticação
 * é feita manualmente no UsuarioService via BCrypt + sessionStorage).
 *
 * Se não tiver spring-security no pom.xml, esta classe é ignorada.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF (necessário para POST/PUT/PATCH sem token CSRF)
                .csrf(AbstractHttpConfigurer::disable)
                // Desabilita autenticação padrão — libera todos os endpoints
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // Desabilita o formulário de login automático do Spring
                .formLogin(AbstractHttpConfigurer::disable)
                // Desabilita HTTP Basic automático
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}