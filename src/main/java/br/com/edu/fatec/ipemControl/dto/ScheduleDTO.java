package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScheduleDTO {
    private Integer vehicleId;
    private Integer serviceTypeId;
    private String title;
    private LocalDateTime scheduledDatetime;
    private String priority;
    private BigDecimal estimatedMileage;
}