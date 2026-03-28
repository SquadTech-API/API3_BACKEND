package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Integer matricula;
    private String nomeCompleto;
    private String email;
    private String cargo;
    private String senha; // senha pura, vai ser criptografada no service
}

