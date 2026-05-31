package br.com.edu.fatec.ipemControl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * NOVO: Configuração global de CORS.
 *
 * Problema identificado: vários controllers não tinham @CrossOrigin,
 * incluindo AbastecimentoController, RelatorioAbastecimentoController,
 * DashboardController, RegistroSaidaController e UploadController.
 *
 * Sem CORS configurado, o browser bloqueia todas as requisições do frontend
 * (rodando em localhost:5500 ou outro domínio) para o backend (localhost:8080)
 * com erro: "Access to fetch at ... has been blocked by CORS policy".
 *
 * Esta configuração aplica CORS globalmente para TODOS os endpoints,
 * eliminando a necessidade de @CrossOrigin em cada controller individualmente.
 *
 * Em produção: substitua origins("*") pelo domínio real do frontend.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Em produção, substitua por: origins("https://seudominio.sp.gov.br")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}