package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VeiculoResumoDTO {

    private Integer id;
    private String modelo;
    private String prefixo;
    private String ultimoUso;
    private String ultimoAbastecimento;
    private String km;
    private String status;
}


