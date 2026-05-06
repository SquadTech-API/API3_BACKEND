package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_uso")
@Data
public class VehicleUsageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vehicleUsageHistoryId;

    private String description;

    private LocalDateTime dateLog;

    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;
}