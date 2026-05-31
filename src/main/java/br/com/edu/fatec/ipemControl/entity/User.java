package br.com.edu.fatec.ipemControl.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration")
    private Integer registration;

    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(name = "driver_license_number", unique = true, length = 20)
    private String driverLicenseNumber;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "user_type", nullable = false,
            columnDefinition = "ENUM('admin','technician') DEFAULT 'technician'")
    @Builder.Default
    private String userType = "technician";

    @Column(name = "role", length = 80)
    private String role;

    @Column(name = "active_employee", nullable = false)
    @Builder.Default
    private boolean activeEmployee = true;

    @Column(name = "license_type",
            columnDefinition = "ENUM('B','C','D','E','AB','AC','AD','AE')")
    private String licenseType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}