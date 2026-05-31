package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.DashboardChartDTO;
import br.com.edu.fatec.ipemControl.repository.VehicleUsageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final VehicleUsageRepository vehicleUsageRepository;

    public DashboardService(VehicleUsageRepository vehicleUsageRepository) {
        this.vehicleUsageRepository = vehicleUsageRepository;
    }

    public DashboardChartDTO findComparison() {
        List<Object[]> results = vehicleUsageRepository.findUsageComparison();

        List<String> labels = new ArrayList<>();
        List<Long> usageCounts = new ArrayList<>();
        List<Double> hours = new ArrayList<>();

        for (Object[] row : results) {
            labels.add((String) row[0]);
            usageCounts.add(((Number) row[1]).longValue());
            hours.add(row[2] != null ? ((Number) row[2]).doubleValue() : 0.0);
        }

        return new DashboardChartDTO(labels, usageCounts, hours);
    }
}
