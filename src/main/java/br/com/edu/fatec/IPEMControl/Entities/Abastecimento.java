package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "abastecimento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Abastecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_abastecimento")
    private Integer idAbastecimento;

    @Column(name = "nota_fiscal", length = 100)
    private String notaFiscal;

    @Column(name = "foto", length = 255)
    private String foto;

    @Column(name = "tipo_combustivel", length = 50)
    private String tipoCombustivel;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Column(name = "km_abastecimento", precision = 10, scale = 2)
    private BigDecimal kmAbastecimento;

    @Column(name = "quantidade_litros", precision = 10, scale = 2)
    private BigDecimal quantidadeLitros;

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "posto_nome", length = 150)
    private String postoNome;

    @Column(name = "posto_cidade", length = 150)
    private String postoCidade;

    @ManyToOne
    @JoinColumn(name = "id_saida")
    private RegistroSaida registroSaida;

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