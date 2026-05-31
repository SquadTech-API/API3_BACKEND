package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScheduleResponseDTO {
    private Integer id;
    private String title;
    private LocalDateTime scheduledDatetime;
    private String priority;
    private String status;
    private String rejectionReason;
    private BigDecimal estimatedMileage;
    private Integer requesterRegistration;
    private String requesterName;
    private Integer vehicleId;
    private String vehiclePrefix;
    private String vehicleModel;
    private Integer serviceTypeId;
    private String serviceTypeName;
    private Integer approverRegistration;
    private String approverName;
    private LocalDateTime createdAt;
}