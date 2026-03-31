package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "veiculo")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private String combustivel;
    private String status;

    @Column(name = "km_atual")
    private Double km;

    @Column(name = "ultima_revisao")
    private LocalDate ultimaRevisao;

    @Column(name = "proxima_revisao")
    private LocalDate proximaRevisao;
}