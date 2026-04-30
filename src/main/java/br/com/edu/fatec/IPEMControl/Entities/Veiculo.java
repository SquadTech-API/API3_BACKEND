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

    @Column(name = "prefixo", nullable = false, length = 20)
    private String prefixo;

    @Column(name = "nucleo_dar", nullable = false, length = 100)
    private String nucleoDar;

    @Column(name = "placa", nullable = false, unique = true, length = 10)
    private String placa;

    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @Column(name = "marca", nullable = false, length = 100)
    private String marca;

    @Column(name = "ano", nullable = false)
    private Integer ano;

    @Column(name = "tipo_combustivel", nullable = false, length = 50)
    private String tipoCombustivel;

    @Column(name = "habilitacao_categoria", nullable = false, length = 10)
    private String habilitacaoCategoria;

    @Column(name = "km_atual", nullable = false, precision = 10, scale = 2)
    private BigDecimal kmAtual;

    @Column(name = "disponivel", nullable = false)
    private Boolean disponivel = true;

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