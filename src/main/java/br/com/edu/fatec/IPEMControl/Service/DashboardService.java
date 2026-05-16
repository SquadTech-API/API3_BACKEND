package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.DashboardGraficoDTO;
import br.com.edu.fatec.IPEMControl.Repository.VehicleUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private VehicleUsageRepository vehicleUsageRepository;

    public DashboardGraficoDTO buscarComparativo() {
        List<Object[]> resultados = vehicleUsageRepository.buscarComparativoUso();

        List<String> labels = new ArrayList<>();
        List<Long> usos = new ArrayList<>();
        List<Double> horas = new ArrayList<>();

        for (Object[] row : resultados) {
            labels.add((String) row[0]);
            usos.add(((Number) row[1]).longValue());
            horas.add(row[2] != null ? ((Number) row[2]).doubleValue() : 0.0);
        }

        return new DashboardGraficoDTO(labels, usos, horas);
    }
}