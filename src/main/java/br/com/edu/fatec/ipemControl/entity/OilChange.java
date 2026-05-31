package br.com.edu.fatec.ipemControl.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "oil_change")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OilChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_log_id", nullable = false)
    private DepartureLog departureLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "change_mileage", nullable = false, precision = 10, scale = 2)
    private BigDecimal changeMileage;

    @Column(name = "interval_km", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal intervalKm = new BigDecimal("5000.00");

    @Column(name = "next_change_mileage", nullable = false, precision = 10, scale = 2)
    private BigDecimal nextChangeMileage;

    @Column(name = "change_date", nullable = false)
    private LocalDate changeDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "alert_sent", nullable = false)
    @Builder.Default
    private boolean alertSent = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}