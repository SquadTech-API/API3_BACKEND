package br.com.edu.fatec.IPEMControl.DTO;

import br.com.edu.fatec.IPEMControl.Entities.Usuario;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioDTO {

    private Integer matricula;
    private String cpf;
    private String numeroHabilitacao;
    private String nome;
    private LocalDate dataNascimento;
    private String email;
    private String senha;
    private Usuario.TipoUsuario tipoUsuario;
    private String cargo;
    private Boolean colaboradorAtivo;
    private Usuario.TipoHabilitacao tipoHabilitacao;
}