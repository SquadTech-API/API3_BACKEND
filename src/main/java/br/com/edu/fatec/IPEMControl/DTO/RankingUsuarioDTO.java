package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RankingUsuarioDTO {
    private String nome;
    private Integer quantidadeAbastecimentos;
    private BigDecimal totalGasto;
}