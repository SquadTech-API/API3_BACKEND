package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;

@Data
public class ServiceTypeDTO {
    private Integer serviceTypeId;
    private String serviceName;
    private String description;
    private Boolean licensed;
    private Boolean oilChange;
}
