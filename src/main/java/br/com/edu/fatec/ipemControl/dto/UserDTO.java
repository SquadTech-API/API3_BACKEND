package br.com.edu.fatec.ipemControl.dto;

import br.com.edu.fatec.ipemControl.entities.User;
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
    private User.UserType userType;
    private String role;
    private Boolean activeEmployee;
    private User.DriverLicenseType driverLicenseType;
}