package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OilChangeDTO {

    // Veículo relacionado (obrigatório)
    private Integer vehicleId;

    // Saída vinculada (opcional — pode ser troca avulsa)
    private Integer departureId;

    // KM no momento da troca (obrigatório)
    private BigDecimal oilChangeMileage;

    // Intervalo em mileage até a próxima troca (ex: 5000)
    private BigDecimal intervalKm;

    // KM da próxima troca = oilChangeMileage + intervalKm (calculado ou enviado pelo frontend)
    private BigDecimal nextOilChangeMileage;

    // Data efetiva da troca (obrigatório)
    private LocalDate oilChangeDate;

    // Observações sobre a troca (type de óleo, filtro, etc.)
    private String observations;
}