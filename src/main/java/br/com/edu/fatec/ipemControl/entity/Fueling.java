package br.com.edu.fatec.ipemControl.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fueling")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Fueling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    @Column(name = "receipt_url", length = 500)
    private String receiptUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;

    @Column(name = "fueling_datetime", nullable = false)
    private LocalDateTime fuelingDatetime;

    @Column(name = "mileage_at_fueling", nullable = false, precision = 10, scale = 2)
    private BigDecimal mileageAtFueling;

    @Column(name = "liters", nullable = false, precision = 8, scale = 3)
    private BigDecimal liters;

    @Column(name = "total_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalValue;

    @Column(name = "station_name", length = 150)
    private String stationName;

    @Column(name = "station_city", length = 150)
    private String stationCity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_log_id", nullable = false)
    private DepartureLog departureLog;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}