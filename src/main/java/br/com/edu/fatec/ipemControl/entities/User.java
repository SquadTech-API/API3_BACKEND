package br.com.edu.fatec.ipemControl.entities;

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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration")
    private Integer registration;

    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(name = "numero_habilitacao", unique = true, length = 20)
    private String driverLicenseNumber;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false, length = 10)
    private UserType userType = UserType.technician;

    @Column(name = "role", length = 80)
    private String position;

    @Column(name = "colaborador_ativo", nullable = false)
    private Boolean activeColaborator = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_habilitacao", length = 2)
    private DriverLicenseType driverLicenseType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ── Enums internos ────────────────────────────────────────────────────────

    public enum UserType {
        adm, technician
    }

    public enum DriverLicenseType {
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