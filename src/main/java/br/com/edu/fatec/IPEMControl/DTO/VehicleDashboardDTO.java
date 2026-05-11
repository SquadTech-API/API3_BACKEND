package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
@Data
@AllArgsConstructor
public class VehicleDashboardDTO {

    private Integer id;
    private String model;
    private String prefix;
    private Map<String, DadosVeiculoDashboardDTO> data;
    private VehicleMaintenanceDTO maintenance;

}