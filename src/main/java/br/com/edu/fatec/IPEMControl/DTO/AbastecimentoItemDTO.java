package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AbastecimentoItemDTO {
    private LocalDateTime dataHora;
    private String veiculo;
    private String responsavel;
    private String tipoCombustivel;
    private BigDecimal quantidadeLitros;
    private BigDecimal valorTotal;
    private BigDecimal kmAbastecimento;
    private String postoNome;
    private String postoCidade;
    private String notaFiscal;
}