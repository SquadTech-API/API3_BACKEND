package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "VEICULO")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private String combustivel;
    private String status; // Ex: Ativo, Inativo, Manutenção

    @Column(name = "KM_ATUAL")
    private Double kmAtual;

    @Column(name = "ULTIMA_REVISAO")
    private LocalDate ultimaRevisao;

    @Column(name = "PROXIMA_REVISAO")
    private LocalDate proximaRevisao;
}