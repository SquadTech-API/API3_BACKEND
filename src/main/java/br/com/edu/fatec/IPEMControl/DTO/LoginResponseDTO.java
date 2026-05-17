package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;

@Data
public class LoginResponseDTO {

    private Integer registrationNumber;

    // CORRIGIDO: campo era "nomeCompleto" mas o frontend espera tanto "name" quanto "nomeCompleto"
    private String fullName;
    private String name; // alias for frontend compatibility

    private String role;
    private String email;
    private String userType;

    // CORRIGIDO: campo ausente — o frontend usa tipoHabilitacao para filtrar veículos
    private String licenseType;

    // CORRIGIDO: campo ausente — activeEmployee necessário para validação no frontend
    private Boolean activeEmployee;

    public LoginResponseDTO() {}

    public LoginResponseDTO(Integer registrationNumber, String fullName, String role,
                            String email, String userType, String licenseType,
                            Boolean activeEmployee) {
        this.registrationNumber       = registrationNumber;
        this.fullName    = fullName;
        this.name            = fullName; // alias
        this.role           = role;
        this.email           = email;
        this.userType     = userType;
        this.licenseType = licenseType;
        this.activeEmployee = activeEmployee;
    }
}