package br.com.edu.fatec.IPEMControl.Exception;

/**
 * Lançada quando uma regra de negócio é violada (→ HTTP 400).
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}