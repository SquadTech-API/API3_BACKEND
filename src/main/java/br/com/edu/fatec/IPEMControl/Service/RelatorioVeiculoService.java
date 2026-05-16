package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.VehicleReportDTO;
import br.com.edu.fatec.IPEMControl.Entities.DepartureLog;
import br.com.edu.fatec.IPEMControl.Entities.Fueling;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Repository.RefuelingRepository;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RelatorioVeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private RegistroSaidaRepository registroSaidaRepository;

    @Autowired
    private RefuelingRepository refuelingRepository;

    /**
     * CORRIGIDO: antes retornava dados hardcoded (Fiat Uno, ABC-1234...).
     * Agora busca dados reais do banco a partir do vehicleId.
     */
    public VehicleReportDTO gerarRelatorioVeiculo(Integer idVeiculo) {

        Vehicle vehicle = veiculoRepository.findById(idVeiculo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado: " + idVeiculo));

        // Total de saídas concluídas
        List<DepartureLog> saidasConcluidas = registroSaidaRepository
                .findByVeiculoIdVeiculoAndDataHoraSaidaBetween(
                        idVeiculo,
                        LocalDateTime.now().minusYears(5),
                        LocalDateTime.now()
                );

        int totalSaidas = saidasConcluidas.size();

        // KM total rodado somando mileageDriven de todas as saídas concluídas
        BigDecimal kmRodado = saidasConcluidas.stream()
                .filter(s -> s.getDrivenKm() != null)
                .map(DepartureLog::getDrivenKm)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Litros totais abastecidos
        List<Fueling> fuelings = refuelingRepository
                .findByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(idVeiculo);

        BigDecimal totalLitros = fuelings.stream()
                .filter(a -> a.getLitersAmount() != null)
                .map(Fueling::getLitersAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Consumo médio mileage/L
        double consumoMedio = 0.0;
        if (totalLitros.compareTo(BigDecimal.ZERO) > 0) {
            consumoMedio = kmRodado.divide(totalLitros, 2, RoundingMode.HALF_UP).doubleValue();
        }

        VehicleReportDTO dto = new VehicleReportDTO();
        dto.setPrefix(vehicle.getPrefix());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setYear(vehicle.getYear());
        dto.setFuelType(vehicle.getFuelType());
        dto.setMileageDriven(kmRodado.doubleValue());
        dto.setAvgConsumption(consumoMedio);
        dto.setTotalDepartures(totalSaidas);

        return dto;
    }
}