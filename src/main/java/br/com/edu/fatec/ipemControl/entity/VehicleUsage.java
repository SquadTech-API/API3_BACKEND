package br.com.edu.fatec.ipemControl.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "uso_veiculo")
@Data
public class VehicleUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    @ManyToOne
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Technician technician;

    @Column(nullable = false)
    private String vehicle;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
}