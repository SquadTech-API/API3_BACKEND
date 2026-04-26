package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OilChangeItemDTO {
    private LocalDateTime changeDate;
    private String vehiclePlate;
    private BigDecimal odometerAtChange;
    private BigDecimal nextChangeOdometer;
    private BigDecimal currentOdometer;
}