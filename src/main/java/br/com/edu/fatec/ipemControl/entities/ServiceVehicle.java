package br.com.edu.fatec.ipemControl.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "veiculo_servico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servico_veiculo")
    private Integer serviceVehicleId;

    @Column(name = "habilitado")
    private Boolean isLicensed;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descriptionSV;

    @ManyToOne
    @JoinColumn(name = "id_veiculo")
    private Vehicle vehicle;

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