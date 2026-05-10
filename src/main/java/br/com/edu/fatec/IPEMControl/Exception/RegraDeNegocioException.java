package br.com.edu.fatec.IPEMControl.Exception;

/**
 * Lançada quando uma regra de negócio é violada (→ HTTP 400).
 */
public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}