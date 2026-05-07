package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "uso_veiculo")
public class UsoVeiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Technician technician;

    @Column(nullable = false)
    private String veiculo;

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    public UsoVeiculo() {}


    public Long getId() { return id; }

    public Technician getTecnico() { return technician; }
    public void setTecnico(Technician technician) { this.technician = technician; }

    public String getVeiculo() { return veiculo; }
    public void setVeiculo(String veiculo) { this.veiculo = veiculo; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
}