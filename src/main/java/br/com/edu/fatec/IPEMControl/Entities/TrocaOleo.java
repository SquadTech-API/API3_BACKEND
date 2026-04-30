package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "troca_oleo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrocaOleo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_troca_oleo")
    private Integer idTrocaOleo;

    @Column(name = "km_troca", precision = 10, scale = 2)
    private BigDecimal kmTroca;

    @Column(name = "km_proxima_troca", precision = 10, scale = 2)
    private BigDecimal kmProximaTroca;

    @ManyToOne
    @JoinColumn(name = "id_saida")
    private RegistroSaida registroSaida;

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