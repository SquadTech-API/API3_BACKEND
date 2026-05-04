package br.com.edu.fatec.IPEMControl.Exception;

/**
 * Lançada quando um recurso não é encontrado no banco (→ HTTP 404).
 * Já existia no projeto — mantida como estava.
 */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}