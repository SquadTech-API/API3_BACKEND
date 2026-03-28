package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRespostaDTO {
    private Integer matricula;
    private String nomeCompleto;
    private String cargo;
    private String email;
}

