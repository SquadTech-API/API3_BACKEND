package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordem_servico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ordem_servico")
    private Integer idOrdemServico;

    @ManyToOne
    @JoinColumn(name = "id_veiculo", nullable = false)
    private Veiculo veiculo;

    @ManyToOne
    @JoinColumn(name = "id_tipo_servico", nullable = false)
    private TipoServico tipoServico;

    @Column(name = "status", length = 50)
    private String status; // Ex: "ABERTA", "EM_ANDAMENTO", "CONCLUIDA"

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @PrePersist
    public void prePersist() {
        this.dataAbertura = LocalDateTime.now();
        if (this.status == null) {
            this.status = "ABERTA";
        }
    }
}