package br.com.edu.fatec.ipemControl.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "troca_oleo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OilChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_troca_oleo")
    private Integer oilChangeId;

    @Column(name = "km_troca", precision = 10, scale = 2)
    private BigDecimal changeKm;

    @Column(name = "intervalo_km", precision = 10, scale = 2)
    private BigDecimal intervalKm;

    @Column(name = "km_proxima_troca", precision = 10, scale = 2)
    private BigDecimal nextChangeKm;

    @Column(name = "data_troca")
    private LocalDate changeDate;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observation;

    @Column(name = "alerta_enviado", nullable = false)
    private Boolean sendAlert = false;

    @ManyToOne
    @JoinColumn(name = "id_saida")
    private DepartureLog departureLog;

    @ManyToOne
    @JoinColumn(name = "id_veiculo")
    private Vehicle vehicle;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.sendAlert == null) this.sendAlert = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}