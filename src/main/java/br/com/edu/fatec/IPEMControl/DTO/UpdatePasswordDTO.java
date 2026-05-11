package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;

@Data
public class UpdatePasswordDTO {
    private String email;
    private String currentPassword;
    private String newPassword;
}