package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OilChangeDTO {

    // Veículo relacionado (obrigatório)
    private Integer vehicleId;

    // Saída vinculada (opcional — pode ser oilChange avulsa)
    private Integer departureId;

    // KM no momento da oilChange (obrigatório)
    private BigDecimal oilChangeMileage;

    // Intervalo em mileage até a próxima oilChange (ex: 5000)
    private BigDecimal intervalKm;

    // KM da próxima oilChange = oilChangeMileage + intervalKm (calculado ou enviado pelo frontend)
    private BigDecimal nextOilChangeMileage;

    // Data efetiva da oilChange (obrigatório)
    private LocalDate oilChangeDate;

    // Observações sobre a oilChange (type de óleo, filtro, etc.)
    private String observations;
}