package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}