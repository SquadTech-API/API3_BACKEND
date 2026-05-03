package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VeiculoResumoDTO {

    private Integer id;
    private String nome;
    private String prefixo;
    private String ultimoUso;
    private String ultimoMotorista;
    private String ultimoAbastecimento;
    private String kmRodados;
    private String status;
}