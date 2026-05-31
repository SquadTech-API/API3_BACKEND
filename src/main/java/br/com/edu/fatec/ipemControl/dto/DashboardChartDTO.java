package br.com.edu.fatec.ipemControl.dto;

import java.util.List;

public class DashboardChartDTO {
    private List<String> labels;
    private List<Long> usages;
    private List<Double> hours;

    public DashboardChartDTO(List<String> labels,
                             List<Long> usages,
                             List<Double> hours) {
        this.labels = labels;
        this.usages = usages;
        this.hours = hours;
    }

    public List<String> getLabels() {
        return labels;
    }
    public List<Long> getUsages() {
        return usages;
    }
    public List<Double> getHours() {
        return hours;
    }
}