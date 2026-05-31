package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;

@Data
public class UpdatePasswordDTO {
    private String email;
    private String currentPassword;
    private String newPassword;
}