package br.com.edu.fatec.IPEMControl.DTO;

import java.util.List;
import java.util.Map;

public class DashboardVeiculoDTO {

    private Map<String, List<GraficoKmItemDTO>> graficoKm;
    private VeiculoDashboardDTO veiculoPadrao;

    public DashboardVeiculoDTO(Map<String, List<GraficoKmItemDTO>> graficoKm,
                               VeiculoDashboardDTO veiculoPadrao) {
        this.graficoKm = graficoKm;
        this.veiculoPadrao = veiculoPadrao;
    }

    public Map<String, List<GraficoKmItemDTO>> getGraficoKm() {
        return graficoKm;
    }

    public VeiculoDashboardDTO getVeiculoPadrao() {
        return veiculoPadrao;
    }
}