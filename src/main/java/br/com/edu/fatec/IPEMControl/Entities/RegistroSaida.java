package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_saida")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroSaida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_saida")
    private Integer idSaida;

    @Column(name = "local_destino", length = 200)
    private String localDestino;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "data_hora_saida")
    private LocalDateTime dataHoraSaida;

    @Column(name = "data_retorno")
    private LocalDateTime dataRetorno;

    @Column(name = "km_inicial", precision = 10, scale = 2)
    private BigDecimal kmInicial;

    @Column(name = "km_final", precision = 10, scale = 2)
    private BigDecimal kmFinal;

    @Column(name = "km_rodados", precision = 10, scale = 2)
    private BigDecimal kmRodados;

    @ManyToOne
    @JoinColumn(name = "id_veiculo")
    private Veiculo veiculo;

    @ManyToOne
    @JoinColumn(name = "matricula_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_tipo_servico")
    private TipoServico tipoServico;
    
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