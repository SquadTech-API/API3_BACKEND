package com.fatec.api3backend.model;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Tecnico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @CPF(message = "CPF inválido")
    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    private String dataNasc;
    private String setor;

    @NotBlank(message = "A matrícula é obrigatória")
    private String matricula;

    private String habilitacao;
    private String senha;
}