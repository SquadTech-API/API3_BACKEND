package br.com.edu.fatec.ipemControl.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "available", nullable = false)
    @Builder.Default
    private boolean available = true;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "prefix", nullable = false, unique = true, length = 20)
    private String prefix;

    @Column(name = "dar_center", nullable = false, length = 100)
    private String darCenter;

    @Column(name = "license_plate", nullable = false, unique = true, length = 10)
    private String licensePlate;

    @Column(name = "fl_number", length = 30)
    private String flNumber;

    @Column(name = "model", nullable = false, length = 60)
    private String model;

    @Column(name = "brand", nullable = false, length = 60)
    private String brand;

    @Column(name = "manufacture_year", nullable = false)
    private Integer manufactureYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;

    @Column(name = "license_category", nullable = false, length = 10)
    private String licenseCategory;

    @Column(name = "current_mileage", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal currentMileage = BigDecimal.ZERO;

    @Column(name = "oil_change_interval_km", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal oilChangeIntervalKm = new BigDecimal("5000.00");

    @Column(name = "next_oil_change_mileage", precision = 10, scale = 2)
    private BigDecimal nextOilChangeMileage;

    @Column(name = "oil_change_alert_sent", nullable = false)
    @Builder.Default
    private boolean oilChangeAlertSent = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}