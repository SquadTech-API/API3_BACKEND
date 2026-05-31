package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserDTO {
    private Integer registration;
    private String cpf;
    private String licenseNumber;
    private String name;
    private LocalDate birthDate;
    private String email;
    private String password;
    private String userType;
    private String role;
    private Boolean activeEmployee;
    private String licenseType;
}