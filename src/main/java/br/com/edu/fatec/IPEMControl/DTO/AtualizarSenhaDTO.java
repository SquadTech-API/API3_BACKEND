package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;

@Data
public class AtualizarSenhaDTO {
    private String email;
    private String senhaAtual;
    private String novaSenha;
}