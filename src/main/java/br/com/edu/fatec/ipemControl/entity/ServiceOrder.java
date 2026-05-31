package br.com.edu.fatec.ipemControl.entity;

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
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ordem_servico")
    private Integer serviceOrderId;

    @ManyToOne
    @JoinColumn(name = "id_veiculo", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "id_tipo_servico", nullable = false)
    private ServiceType serviceType;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observation;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime openingDate;

    @Column(name = "data_conclusao")
    private LocalDateTime completionDate;

    @PrePersist
    public void prePersist() {
        this.openingDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = "ABERTA";
        }
    }
}