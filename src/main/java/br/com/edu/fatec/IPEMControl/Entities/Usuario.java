package br.com.edu.fatec.IPEMControl.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
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

    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(name = "numero_habilitacao", unique = true, length = 20)
    private String numeroHabilitacao;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @JsonIgnore
    @Column(name = "senha", nullable = false, length = 255)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false, length = 10)
    private TipoUsuario tipoUsuario = TipoUsuario.tecnico;

    @Column(name = "cargo", length = 80)
    private String cargo;

    @Column(name = "colaborador_ativo", nullable = false)
    private Boolean colaboradorAtivo = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_habilitacao", length = 2)
    private TipoHabilitacao tipoHabilitacao;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ── Enums internos ────────────────────────────────────────────────────────

    public enum TipoUsuario {
        adm, tecnico
    }

    public enum TipoHabilitacao {
        B, C, D, E, AB, AC, AD, AE
    }

    // ── Lifecycle callbacks ───────────────────────────────────────────────────

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