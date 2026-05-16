package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.*;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Repository.RefuelingRepository;
import br.com.edu.fatec.IPEMControl.Repository.ExitRecordRepository;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class DashboardVeiculoService {

    @Autowired
    private ExitRecordRepository exitRecordRepository;

    @Autowired
    private RefuelingRepository refuelingRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    public DashboardVeiculoDTO buscarDashboard() {

        List<GraficoKmItemDTO> topSemana = exitRecordRepository.buscarTop5KmSemana()
                .stream()
                .map(obj -> new GraficoKmItemDTO(
                        ((Number) obj[0]).intValue(),
                        (String) obj[1],
                        ((Number) obj[2]).doubleValue()
                ))
                .toList();

        if (topSemana.isEmpty()) {
            return new DashboardVeiculoDTO(Map.of("semana", new ArrayList<>()), null);
        }

        Integer idVeiculoPadrao = topSemana.get(0).getId();

        VehicleDashboardDTO veiculoPadrao = montarVeiculo(idVeiculoPadrao);

        return new DashboardVeiculoDTO(
                Map.of("semana", topSemana),
                veiculoPadrao
        );
    }

    private VehicleDashboardDTO montarVeiculo(Integer idVeiculo) {

        Vehicle v = veiculoRepository.findById(idVeiculo).orElseThrow();

        Double gasto = refuelingRepository.totalGastoSemana(idVeiculo);
        Double litros = refuelingRepository.totalLitrosSemana(idVeiculo);
        Double km = exitRecordRepository.totalKmSemana(idVeiculo);
        Long saidas = exitRecordRepository.totalSaidasSemana(idVeiculo);

        Double consumo = (litros != null && litros > 0) ? km / litros : 0.0;

        DadosVeiculoDashboardDTO dados =
                new DadosVeiculoDashboardDTO(gasto, litros, km, saidas, consumo);

        return new VehicleDashboardDTO(
                v.getVehicleId(),
                v.getModel(),
                v.getPrefix(),
                Map.of("semana", dados),
                new VehicleMaintenanceDTO(
                        v.getCurrentKm() != null ? v.getCurrentKm().doubleValue() : 0.0,
                        100000.0
                )
        );
    }
}