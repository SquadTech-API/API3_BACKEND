package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Integer registration;
    private String cpf;
    private String licenseNumber;
    private String name;
    private LocalDate birthDate;
    private String email;
    private String userType;
    private String role;
    private Boolean activeEmployee;
    private String licenseType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}