package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class RelatorioDiarioDTO {
    private Integer matricula;
    private String nomeTecnico;
    private LocalDate dataRelatorio;
    private int totalAtividades;
    private BigDecimal quilometragemTotalDia;
    private List<AtividadeDiariaDTO> atividades;
}