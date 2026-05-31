package br.com.edu.fatec.ipemControl.dto;

import java.util.Map;

public class VehicleDashboardDTO {

    private Integer id;
    private String model;
    private String prefix;
    private Map<String, VehicleDashboardDataDTO> data;
    private VehicleMaintenanceDTO maintenance;

    public VehicleDashboardDTO(Integer id,
                               String model,
                               String prefix,
                               Map<String, VehicleDashboardDataDTO> data,
                               VehicleMaintenanceDTO maintenance) {

        this.id = id;
        this.model = model;
        this.prefix = prefix;
        this.data = data;
        this.maintenance = maintenance;
    }

    public Integer getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getPrefix() {
        return prefix;
    }

    public Map<String, VehicleDashboardDataDTO> getData() {
        return data;
    }

    public VehicleMaintenanceDTO getMaintenance() {
        return maintenance;
    }
}