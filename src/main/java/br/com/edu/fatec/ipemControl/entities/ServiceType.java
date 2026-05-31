package br.com.edu.fatec.ipemControl.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_servico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_servico")
    private Integer serviceTypeId;

    @Column(name = "nome_servico", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String description;

    @Column(name = "habilitado", nullable = false)
    private Boolean licensed = true;

    @Column(name = "eh_troca_oleo", nullable = false)
    private Boolean oilChangeST = false;
}