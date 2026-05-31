package br.com.edu.fatec.ipemControl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleSummaryDTO {
    private Integer id;
    private String model;
    private String prefix;
    private String lastUsage;
    private String lastDriver;
    private String lastRefuel;
    private String mileage;
    private String status;
    private String licenseCategory;
    private Boolean active;

    public VehicleSummaryDTO(Integer id, String model, String prefix,
                             String lastUsage, String lastDriver,
                             String lastRefuel, String mileage, String status) {
        this.id = id;
        this.model = model;
        this.prefix = prefix;
        this.lastUsage = lastUsage;
        this.lastDriver = lastDriver;
        this.lastRefuel = lastRefuel;
        this.mileage = mileage;
        this.status = status;
        this.active = true;
    }
}