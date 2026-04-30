package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankingPostoDTO {
    private String nomePosto;
    private String cidadePosto;
    private Long quantidadeVisitas;
}