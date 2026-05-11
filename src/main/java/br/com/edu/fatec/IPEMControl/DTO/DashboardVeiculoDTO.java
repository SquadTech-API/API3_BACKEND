package br.com.edu.fatec.IPEMControl.DTO;

import java.util.List;
import java.util.Map;

public class DashboardVeiculoDTO {

    private Map<String, List<KilometerChartItemDTO>> graficoKm;
    private VehicleDashboardDTO veiculoPadrao;

    public DashboardVeiculoDTO(Map<String, List<KilometerChartItemDTO>> graficoKm,
                               VehicleDashboardDTO veiculoPadrao) {
        this.graficoKm = graficoKm;
        this.veiculoPadrao = veiculoPadrao;
    }

    public Map<String, List<KilometerChartItemDTO>> getGraficoKm() {
        return graficoKm;
    }

    public VehicleDashboardDTO getVeiculoPadrao() {
        return veiculoPadrao;
    }
}