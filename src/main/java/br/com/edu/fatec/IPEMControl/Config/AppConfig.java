package br.com.edu.fatec.IPEMControl.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * NOVO: Configuração de beans da aplicação.
 *
 * Problema identificado: BCryptPasswordEncoder era injetado via @Autowired
 * em UsuarioService mas não havia nenhum @Bean definindo esse objeto.
 * Isso causaria NoSuchBeanDefinitionException ao iniciar o Spring Boot,
 * impedindo toda a aplicação de subir.
 *
 * Colocamos aqui junto com o WebConfig para manter a organização.
 */
@Configuration
public class AppConfig {

    /**
     * Bean do BCryptPasswordEncoder — necessário para codificar e validar senhas.
     * Injetado em UsuarioService via @Autowired.
     * Fator padrão: 10 (balanceia segurança e performance).
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}