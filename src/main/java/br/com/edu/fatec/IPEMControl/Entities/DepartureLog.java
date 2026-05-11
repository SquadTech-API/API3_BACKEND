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
public class DepartureLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_saida")
    private Integer departureLogId;

    @Column(name = "local_destino", length = 200)
    private String destination;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "data_hora_saida")
    private LocalDateTime dateTimeDeparture;

    @Column(name = "data_retorno")
    private LocalDateTime returnDate;

    @Column(name = "km_inicial", precision = 10, scale = 2)
    private BigDecimal startingKm;

    @Column(name = "km_final", precision = 10, scale = 2)
    private BigDecimal finishingKm;

    @Column(name = "km_rodados", precision = 10, scale = 2)
    private BigDecimal drivenKm;

    @ManyToOne
    @JoinColumn(name = "id_veiculo")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "matricula_usuario")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_tipo_servico")
    private ServiceType serviceType;
    
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