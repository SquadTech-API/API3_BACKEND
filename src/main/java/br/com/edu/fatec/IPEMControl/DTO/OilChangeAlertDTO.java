
package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class OilChangeAlertDTO {

    private Integer vehicleId;
    private String prefix;
    private String model;
    private String licensePlate;

    private BigDecimal currentMileage;
    private BigDecimal lastChangeMileage;
    private BigDecimal nextChangeMileage;

    private BigDecimal kmOverdue;

    private LocalDate lastChangeDate;

    private String alertType;
}

