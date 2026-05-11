package br.com.edu.fatec.IPEMControl.DTO;

public class VehicleMaintenanceDTO {

    private Double currentMileage;
    private Double nextOilChange;

    public VehicleMaintenanceDTO(Double currentMileage, Double nextOilChange) {
        this.currentMileage = currentMileage;
        this.nextOilChange = nextOilChange;
    }

    public Double getCurrentMileage() {
        return currentMileage;
    }

    public Double getNextOilChange() {
        return nextOilChange;
    }
}