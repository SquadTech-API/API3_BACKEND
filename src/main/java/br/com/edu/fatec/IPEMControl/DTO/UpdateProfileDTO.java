package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String name;
    private String currentPassword;
    private String newPassword;
}
