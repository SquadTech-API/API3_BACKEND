package br.com.edu.fatec.IPEMControl.Exception;

/**
 * Lançada quando um recurso não é encontrado no banco (→ HTTP 404).
 * Já existia no projeto — mantida como estava.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}