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
    @Column(name = "id_veiculo") // Corrigido: Casando com seu SQL
    private Integer id;

    @Column(nullable = false)
    private String prefixo;

    @Column(name = "nucleo_dar", nullable = false)
    private String nucleoDar;

    @Column(name = "habilitacao_categoria", nullable = false)
    private String habilitacaoCategoria;

    @Column(nullable = false, unique = true)
    private String placa;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private Integer ano;

    @Column(name = "tipo_combustivel", nullable = false)
    private String tipoCombustivel;

    @Column(name = "km_atual", nullable = false)
    private Double km;

    @Column(name = "disponivel", nullable = false)
    private Boolean disponivel = true;

    @Column(name = "created_at", updatable = false, insertable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at", updatable = false, insertable = false)
    private java.time.LocalDateTime updatedAt;

}