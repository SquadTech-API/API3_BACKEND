package br.com.edu.fatec.ipemControl.dto;

import java.util.List;
import java.util.Map;

public class VehicleDashboardResponseDTO {

    private Map<String, List<KilometerChartItemDTO>> kilometerChart;
    private VehicleDashboardDTO defaultVehicle;

    public VehicleDashboardResponseDTO(
            Map<String, List<KilometerChartItemDTO>> kilometerChart,
            VehicleDashboardDTO defaultVehicle) {

        this.kilometerChart = kilometerChart;
        this.defaultVehicle = defaultVehicle;
    }

    public Map<String, List<KilometerChartItemDTO>> getKilometerChart() {
        return kilometerChart;
    }

    public VehicleDashboardDTO getDefaultVehicle() {
        return defaultVehicle;
    }
}