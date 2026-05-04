package br.com.edu.fatec.IPEMControl.DTO;

import java.util.List;

public class DashboardGraficoDTO {

    private List<String> labels;
    private List<Long> usos;
    private List<Double> horas;

    public DashboardGraficoDTO(List<String> labels, List<Long> usos, List<Double> horas) {
        this.labels = labels;
        this.usos = usos;
        this.horas = horas;
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<Long> getUsos() {
        return usos;
    }

    public List<Double> getHoras() {
        return horas;
    }
}