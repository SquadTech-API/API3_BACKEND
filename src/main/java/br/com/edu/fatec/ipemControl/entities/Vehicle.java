package br.com.edu.fatec.ipemControl.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_veiculo")
    private Integer vehicleId;

    @Column(name = "prefix", nullable = false, length = 20)
    private String prefix;

    @Column(name = "nucleo_dar", nullable = false, length = 100)
    private String darCenter;

    @Column(name = "licensePlate", nullable = false, unique = true, length = 10)
    private String licensePlate;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "brand", nullable = false, length = 100)
    private String brand;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "tipo_combustivel", nullable = false, length = 50)
    private String fuelType;

    @Column(name = "habilitacao_categoria", nullable = false, length = 10)
    private String licenseCategory;

    @Column(name = "km_atual", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentKm;

    @Column(name = "disponivel", nullable = false)
    private Boolean available = true;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "numero_fl", length = 30)
    private String flNumber;

    @Column(name = "intervalo_troca_oleo_km", precision = 10, scale = 2)
    private BigDecimal oilChangeIntervalKm;

    @Column(name = "alerta_troca_oleo_enviado", nullable = false)
    private Boolean oilChangeAlertSent = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.active == null) this.active = true;
        if (this.available == null) this.available = true;
        if (this.oilChangeAlertSent == null) this.oilChangeAlertSent = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}