package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {

    private Integer registration;

    private String fullName;
    private String name; // alias for frontend compatibility

    private String role;
    private String email;
    private String userType;

    private String licenseType;

    private Boolean activeEmployee;

    private String token;

    public LoginResponseDTO() {}

    public LoginResponseDTO(Integer registration, String fullName, String role,
                            String email, String userType, String licenseType,
                            Boolean activeEmployee, String token) {
        this.registration  = registration;
        this.fullName      = fullName;
        this.name          = fullName; // alias
        this.role          = role;
        this.email         = email;
        this.userType      = userType;
        this.licenseType   = licenseType;
        this.activeEmployee = activeEmployee;
        this.token         = token;
    }
}