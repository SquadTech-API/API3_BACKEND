package br.com.edu.fatec.ipemControl.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fuel_type")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FuelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 60)
    private String name;

    @Column(name = "abbreviation", nullable = false, unique = true, length = 10)
    private String abbreviation;

    @Column(name = "category", length = 40)
    private String category;

    @Column(name = "price_per_liter", precision = 8, scale = 3)
    private BigDecimal pricePerLiter;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}