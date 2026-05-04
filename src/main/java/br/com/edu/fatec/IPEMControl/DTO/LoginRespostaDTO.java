package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;

@Data
public class LoginRespostaDTO {

    private Integer matricula;

    // CORRIGIDO: campo era "nomeCompleto" mas o frontend espera tanto "nome" quanto "nomeCompleto"
    private String nomeCompleto;
    private String nome; // alias para compatibilidade com o frontend

    private String cargo;
    private String email;
    private String tipoUsuario;

    // CORRIGIDO: campo ausente — o frontend usa tipoHabilitacao para filtrar veículos
    private String tipoHabilitacao;

    // CORRIGIDO: campo ausente — colaboradorAtivo necessário para validação no frontend
    private Boolean colaboradorAtivo;

    public LoginRespostaDTO() {}

    public LoginRespostaDTO(Integer matricula, String nomeCompleto, String cargo,
                            String email, String tipoUsuario, String tipoHabilitacao,
                            Boolean colaboradorAtivo) {
        this.matricula       = matricula;
        this.nomeCompleto    = nomeCompleto;
        this.nome            = nomeCompleto; // alias
        this.cargo           = cargo;
        this.email           = email;
        this.tipoUsuario     = tipoUsuario;
        this.tipoHabilitacao = tipoHabilitacao;
        this.colaboradorAtivo = colaboradorAtivo;
    }
}