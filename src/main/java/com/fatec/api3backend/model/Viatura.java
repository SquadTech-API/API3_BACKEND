package com.fatec.api3backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "viaturas")
@Data // Mantemos para não quebrar outras partes, mas vamos garantir os métodos abaixo
public class Viatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Núcleo/DAR é obrigatório")
    @Column(nullable = false)
    private String nucleoDar;

    @NotBlank(message = "Número FL é obrigatório")
    @Column(nullable = false)
    private String numeroFl;

    @NotBlank(message = "Marca/Modelo é obrigatório")
    @Column(nullable = false)
    private String marcaModelo;

    @NotBlank(message = "Tipo de combustível é obrigatório")
    @Column(nullable = false)
    private String tipoCombustivel;

    @NotBlank(message = "Placa é obrigatória")
    @Column(nullable = false, unique = true)
    private String placa;

    @NotBlank(message = "Prefixo é obrigatório")
    @Column(nullable = false)
    private String prefixo;

    // --- MÉTODOS MANUAIS PARA DESTRAVAR O COMPILADOR ---

    public String getPlaca() {
        return this.placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}