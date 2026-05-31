package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartureLogResponseDTO {
    private Integer id;
    private String destination;
    private String status;
    private String notes;
    private LocalDateTime departureDatetime;
    private LocalDateTime returnDatetime;
    private BigDecimal startingMileage;
    private BigDecimal finishingMileage;
    private BigDecimal drivenMileage;
    private Boolean sgiTranscribed;
    private Integer vehicleId;
    private String vehiclePrefix;
    private String vehicleModel;
    private String vehicleLicensePlate;
    private Integer userRegistration;
    private String userName;
    private Integer secondUserRegistration;
    private String secondUserName;
    private Integer serviceTypeId;
    private String serviceName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}