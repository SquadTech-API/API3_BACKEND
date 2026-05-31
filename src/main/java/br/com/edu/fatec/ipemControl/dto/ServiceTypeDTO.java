package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;

@Data
public class ServiceTypeDTO {
    private Integer id;
    private String serviceName;
    private String description;
    private Boolean enabled;
    private Boolean oilChange;
}