package br.com.edu.fatec.ipemControl.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_registration")
    private Integer userRegistration;

    @Column(name = "user_name", length = 120)
    private String userName;

    @Column(name = "entity", nullable = false, length = 60)
    private String entity;

    @Column(name = "entity_id")
    private Integer entityId;

    @Column(name = "action", nullable = false,
            columnDefinition = "ENUM('CREATE','UPDATE','DELETE','ACTIVATE','DEACTIVATE','LOGIN','APPROVE','REJECT')")
    private String action;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}