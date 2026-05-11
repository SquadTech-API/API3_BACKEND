package br.com.edu.fatec.IPEMControl.Exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * NOVO: Handler global de exceções.
 * Antes os erros retornavam stack trace HTML — agora retornam JSON com campo "message".
 * O frontend usa response.json().message para exibir nos modais de erro.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 — Recurso não encontrado
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(404).body(Map.of("message", ex.getMessage()));
    }

    // 400 — Regra de negócio violada
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Map<String, String>> handleRegraDeNegocio(RegraDeNegocioException ex) {
        return ResponseEntity.status(400).body(Map.of("message", ex.getMessage()));
    }

    // 409 — Violação de integridade (ex: CPF duplicado, licensePlate duplicada)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleIntegridade(DataIntegrityViolationException ex) {
        String msg = "Dados duplicados ou violação de integridade.";
        String cause = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "";
        if (cause.contains("cpf"))           msg = "CPF já cadastrado no sistema.";
        else if (cause.contains("email"))    msg = "E-mail já cadastrado no sistema.";
        else if (cause.contains("licensePlate"))    msg = "Placa já cadastrada no sistema.";
        else if (cause.contains("numero_habilitacao")) msg = "Número de habilitação já cadastrado.";
        else if (cause.contains("nome_servico"))       msg = "Já existe um type de serviço com esse name.";
        return ResponseEntity.status(409).body(Map.of("message", msg));
    }

    // 500 — Erro genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenerico(Exception ex) {
        return ResponseEntity.status(500).body(
                Map.of("message", "Erro interno do servidor: " + ex.getMessage())
        );
    }
}