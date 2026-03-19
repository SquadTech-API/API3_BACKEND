package com.fatec.api3backend.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data // Esta anotação do Lombok cria os Getters e Setters automaticamente
public class Viatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campos solicitados pelo cliente
    private String nucleoDar;      // núcleo/Dar
    private String numeroFl;       // numero.fl.
    private String marcaModelo;    // Marca e modelo
    private String tipoCombustivel; // tipo de combustível aceito
    private String placa;          // Placas
    private String prefixo;        // Prefixo
}