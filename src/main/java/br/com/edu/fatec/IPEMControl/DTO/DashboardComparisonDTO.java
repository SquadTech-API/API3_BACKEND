package br.com.edu.fatec.IPEMControl.DTO;

public class DashboardComparisonDTO {
    private String vehicle;
    private Long totalUsages;
    private Double usageHours;

    public DashboardComparisonDTO(String vehicle,
                                  Long totalUsages,
                                  Double usageHours) {

        this.vehicle = vehicle;
        this.totalUsages = totalUsages;
        this.usageHours = usageHours;
    }
    public String getVehicle() {
        return vehicle;
    }
    public Long getTotalUsages() {
        return totalUsages;
    }
    public Double getUsageHours() {
        return usageHours;
    }
}