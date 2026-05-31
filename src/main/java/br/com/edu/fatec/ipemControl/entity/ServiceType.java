package br.com.edu.fatec.ipemControl.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_type")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "service_name", nullable = false, unique = true, length = 100)
    private String serviceName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "is_oil_change", nullable = false)
    @Builder.Default
    private boolean isOilChange = false;
}