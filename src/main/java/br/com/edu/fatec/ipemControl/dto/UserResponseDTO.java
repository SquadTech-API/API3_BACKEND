package br.com.edu.fatec.ipemControl.dto;

import br.com.edu.fatec.ipemControl.entities.User;
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
    private User.UserType userType;
    private String role;
    private Boolean activeEmployee;
    private User.DriverLicenseType driverLicenseType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
