package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "troca_oleo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrocaOleo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_troca_oleo")
    private Integer idTrocaOleo;

    @Column(name = "km_troca", precision = 10, scale = 2)
    private BigDecimal kmTroca;

    // CORRIGIDO: campo ausente — intervalo entre trocas em km
    @Column(name = "intervalo_km", precision = 10, scale = 2)
    private BigDecimal intervaloKm;

    @Column(name = "km_proxima_troca", precision = 10, scale = 2)
    private BigDecimal kmProximaTroca;

    // CORRIGIDO: campo ausente — data efetiva da troca
    @Column(name = "data_troca")
    private LocalDate dataTroca;

    // CORRIGIDO: campo ausente — observações sobre a troca
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    // CORRIGIDO: campo ausente — se alerta de troca foi enviado
    @Column(name = "alerta_enviado", nullable = false)
    private Boolean alertaEnviado = false;

    // Vínculo com a saída (pode ser nulo — troca avulsa)
    @ManyToOne
    @JoinColumn(name = "id_saida")
    private RegistroSaida registroSaida;

    // CORRIGIDO: vínculo direto com o veículo (campo id_veiculo no banco)
    @ManyToOne
    @JoinColumn(name = "id_veiculo")
    private Vehicle vehicle;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.alertaEnviado == null) this.alertaEnviado = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}