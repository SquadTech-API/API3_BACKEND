package br.com.edu.fatec.IPEMControl.DTO;

import br.com.edu.fatec.IPEMControl.Entities.User;
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
    private User.UserType userType;
    private String cargo;
    private Boolean colaboradorAtivo;
    private User.DriverLicenseType driverLicenseType;
}