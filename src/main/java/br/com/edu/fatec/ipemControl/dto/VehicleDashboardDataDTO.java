package br.com.edu.fatec.ipemControl.dto;

public class VehicleDashboardDataDTO {
    private Double totalCost;
    private Double totalLiters;
    private Double totalKilometers;
    private Long totalTrips;
    private Double averageConsumption;

    public VehicleDashboardDataDTO(Double totalCost,
                                   Double totalLiters,
                                   Double totalKilometers,
                                   Long totalTrips,
                                   Double averageConsumption) {

        this.totalCost = totalCost;
        this.totalLiters = totalLiters;
        this.totalKilometers = totalKilometers;
        this.totalTrips = totalTrips;
        this.averageConsumption = averageConsumption;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public Double getTotalLiters() {
        return totalLiters;
    }

    public Double getTotalKilometers() {
        return totalKilometers;
    }

    public Long getTotalTrips() {
        return totalTrips;
    }

    public Double getAverageConsumption() {
        return averageConsumption;
    }
}