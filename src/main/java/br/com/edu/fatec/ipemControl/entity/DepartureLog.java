package br.com.edu.fatec.ipemControl.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "departure_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DepartureLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "destination", nullable = false, length = 200)
    private String destination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceType;

    @Column(name = "status", nullable = false,
            columnDefinition = "ENUM('in_progress','completed') DEFAULT 'in_progress'")
    @Builder.Default
    private String status = "in_progress";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "departure_datetime", nullable = false)
    private LocalDateTime departureDatetime;

    @Column(name = "return_datetime")
    private LocalDateTime returnDatetime;

    @Column(name = "starting_mileage", nullable = false, precision = 10, scale = 2)
    private BigDecimal startingMileage;

    @Column(name = "finishing_mileage", precision = 10, scale = 2)
    private BigDecimal finishingMileage;

    @Column(name = "driven_mileage", precision = 10, scale = 2)
    private BigDecimal drivenMileage;

    @Column(name = "sgi_transcribed", nullable = false)
    @Builder.Default
    private boolean sgiTranscribed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    // Condutor principal
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_registration", nullable = false)
    private User user;

    // 2º condutor opcional
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_user_registration", nullable = true)
    private User secondUser;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}