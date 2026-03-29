package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @Column(name = "matricula")
    private Integer matricula;

    @Column(name = "cpf", length = 14)
    private String cpf;

    @Column(name = "numero_habilitacao", length = 20)
    private String numeroHabilitacao;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha", nullable = false, length = 255)
    private String senha;

    @Column(name = "tipo_usuario", length = 50)
    private String tipoUsuario;

    @Column(name = "cargo", length = 100)
    private String cargo;

    @Column(name = "colaborador_ativo")
    private Boolean colaboradorAtivo = true;

    @Column(name = "tipo_habilitacao", length = 10)
    private String tipoHabilitacao;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}