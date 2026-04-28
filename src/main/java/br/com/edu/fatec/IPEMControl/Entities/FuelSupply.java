package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fuel_supply")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuelSupply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String invoiceNumber;
    private String photo;
    private String fuelType;
    private LocalDateTime dateTime;
    private BigDecimal odometerReading;
    private BigDecimal litersAmount;
    private BigDecimal totalValue;
    private String stationName;
    private String stationCity;

    @ManyToOne
    @JoinColumn(name = "exit_record_id")
    private RegistroSaida exitRecord;

    private LocalDateTime createdAt;
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