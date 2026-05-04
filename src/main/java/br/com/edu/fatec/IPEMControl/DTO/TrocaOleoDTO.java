package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TrocaOleoDTO {

    // Veículo relacionado (obrigatório)
    private Integer idVeiculo;

    // Saída vinculada (opcional — pode ser troca avulsa)
    private Integer idSaida;

    // KM no momento da troca (obrigatório)
    private BigDecimal kmTroca;

    // Intervalo em km até a próxima troca (ex: 5000)
    private BigDecimal intervaloKm;

    // KM da próxima troca = kmTroca + intervaloKm (calculado ou enviado pelo frontend)
    private BigDecimal kmProximaTroca;

    // Data efetiva da troca (obrigatório)
    private LocalDate dataTroca;

    // Observações sobre a troca (tipo de óleo, filtro, etc.)
    private String observacoes;
}