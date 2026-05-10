package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AbastecimentoDTO {
    private Integer idSaida;
    private LocalDateTime dataHora;
    private String tipoCombustivel;
    private BigDecimal quantidadeLitros;
    private BigDecimal valorTotal;
    private BigDecimal kmAbastecimento;
    private String postoNome;
    private String postoCidade;
    private String notaFiscal;
}