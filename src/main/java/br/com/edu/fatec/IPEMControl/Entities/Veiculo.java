package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "veiculo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_veiculo")
    private Integer idVeiculo;

    @Column(name = "prefixo", length = 20)
    private String prefixo;

    @Column(name = "nucleo_dar", length = 100)
    private String nucleoDar;

    @Column(name = "placa", length = 10)
    private String placa;

    @Column(name = "numero_fl", length = 20)
    private String numeroFl;

    @Column(name = "modelo", length = 100)
    private String modelo;

    @Column(name = "marca", length = 100)
    private String marca;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "tipo_combustivel", length = 50)
    private String tipoCombustivel;

    @Column(name = "habilitacao_categoria", length = 10)
    private String habilitacaoCategoria;

    @Column(name = "km_atual", precision = 10, scale = 2)
    private BigDecimal kmAtual;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}