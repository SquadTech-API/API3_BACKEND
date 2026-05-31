package br.com.edu.fatec.ipemControl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DailyActivityDTO  {
    private String vehiclePrefix;
    private String destinationLocation;
    private LocalDateTime departureTime;
    private LocalDateTime returnTime;
    private BigDecimal traveledKilometers;
    private String status;
}