package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.Entities.FuelSupply;
import br.com.edu.fatec.IPEMControl.Entities.OilMaintenance;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.DTO.*;
import br.com.edu.fatec.IPEMControl.Repository.FuelSupplyRepository;
import br.com.edu.fatec.IPEMControl.Repository.OilMaintenanceRepository;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FuelSupplyService {

    private final FuelSupplyRepository fuelSupplyRepository;
    private final OilMaintenanceRepository oilMaintenanceRepository;
    private final RegistroSaidaRepository exitRecordRepository;
    private final VeiculoRepository vehicleRepository;

    public FuelSupplyService(
            FuelSupplyRepository fuelSupplyRepository,
            OilMaintenanceRepository oilMaintenanceRepository,
            RegistroSaidaRepository exitRecordRepository,
            VeiculoRepository vehicleRepository) {
        this.fuelSupplyRepository = fuelSupplyRepository;
        this.oilMaintenanceRepository = oilMaintenanceRepository;
        this.exitRecordRepository = exitRecordRepository;
        this.vehicleRepository = vehicleRepository;
    }

    // ── POST /abastecimento ──────────────────────────────────────────────────

    public FuelSupplySaveDTO save(FuelSupplyDTO dto) {
        // Busca o registro de saída vinculado
        RegistroSaida exitRecord = exitRecordRepository.findById(dto.getExitRecordId())
                .orElseThrow(() -> new RuntimeException("Registro de saída não encontrado."));

        FuelSupply fuelSupply = new FuelSupply();
        fuelSupply.setExitRecord(exitRecord);
        fuelSupply.setDateTime(dto.getDateTime());
        fuelSupply.setFuelType(dto.getFuelType());
        fuelSupply.setLitersAmount(dto.getLitersAmount());
        fuelSupply.setTotalValue(dto.getTotalValue());
        fuelSupply.setOdometerReading(dto.getOdometerReading());
        fuelSupply.setStationName(dto.getStationName());
        fuelSupply.setStationCity(dto.getStationCity());
        fuelSupply.setInvoiceNumber(dto.getInvoiceNumber());

        FuelSupply saved = fuelSupplyRepository.save(fuelSupply);

        return new FuelSupplySaveDTO(
                saved.getId(),
                saved.getDateTime(),
                saved.getFuelType(),
                saved.getLitersAmount(),
                saved.getTotalValue(),
                saved.getOdometerReading(),
                saved.getStationName(),
                saved.getStationCity(),
                saved.getInvoiceNumber(),
                exitRecord.getIdSaida()
        );
    }

    // ── GET /abastecimento/historico ─────────────────────────────────────────

    public List<FuelSupplyHistoryDTO> findHistory(Integer vehicleId) {
        List<FuelSupply> list = (vehicleId != null)
                ? fuelSupplyRepository.findByExitRecordVehicleIdOrderByDateTimeDesc(vehicleId)
                : fuelSupplyRepository.findAllByOrderByDateTimeDesc();

        return list.stream().map(this::toHistoryDTO).collect(Collectors.toList());
    }

    private FuelSupplyHistoryDTO toHistoryDTO(FuelSupply f) {
        RegistroSaida exitRecord = f.getExitRecord();
        Vehicle vehicle = exitRecord != null ? exitRecord.getVehicle() : null;
        String responsible = exitRecord != null && exitRecord.getUsuario() != null
                ? exitRecord.getUsuario().getNome() : null;

        return new FuelSupplyHistoryDTO(
                f.getId(),
                f.getDateTime(),
                f.getFuelType(),
                f.getLitersAmount(),
                f.getTotalValue(),
                f.getOdometerReading(),
                f.getStationName(),
                f.getStationCity(),
                f.getInvoiceNumber(),
                vehicle != null ? vehicle.getIdVeiculo() : null,
                vehicle != null ? vehicle.getModelo()    : null,
                vehicle != null ? vehicle.getPrefixo()   : null,
                vehicle != null ? vehicle.getPlaca()     : null,
                responsible
        );
    }

    // ── GET /relatorios/abastecimento/geral ──────────────────────────────────

    public FuelSupplyReportDTO generateReport(String period) {
        LocalDateTime startDate = resolveStartDate(period);

        List<FuelSupply> fuelSupplies = fuelSupplyRepository
                .findByDateTimeAfterOrderByDateTimeDesc(startDate);

        // Totais do período
        BigDecimal totalSpent = fuelSupplies.stream()
                .map(f -> f.getTotalValue() != null ? f.getTotalValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLiters = fuelSupplies.stream()
                .map(f -> f.getLitersAmount() != null ? f.getLitersAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Trocas de óleo no período
        List<OilMaintenance> oilMaintenances = oilMaintenanceRepository
                .findByCreatedAtAfterOrderByCreatedAtDesc(startDate);

        // Veículos com manutenção atrasada
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        int overdueCount = 0;
        for (Vehicle v : allVehicles) {
            var latest = oilMaintenanceRepository.findLatestByVehicleId(v.getIdVeiculo());
            if (latest.isPresent() && v.getKmAtual() != null &&
                    v.getKmAtual().compareTo(latest.get().getNextChangeOdometer()) >= 0) {
                overdueCount++;
            }
        }

        // Consumo e custo médio por veículo
        List<Object[]> consumptionRows = fuelSupplyRepository.findConsumptionByVehicle(startDate);
        List<VehicleConsumptionDTO> vehicleConsumption = buildVehicleConsumption(
                consumptionRows, allVehicles);

        double avgConsumption = vehicleConsumption.stream()
                .filter(v -> v.getConsumptionKmL() != null)
                .mapToDouble(VehicleConsumptionDTO::getConsumptionKmL)
                .average().orElse(0);

        double avgCostPerKm = vehicleConsumption.stream()
                .filter(v -> v.getCostPerKm() != null)
                .mapToDouble(VehicleConsumptionDTO::getCostPerKm)
                .average().orElse(0);

        // Gráfico semanal — sempre 4 posições
        List<Object[]> weeklyRows = fuelSupplyRepository.findWeeklyStats(startDate);
        List<BigDecimal> weeklySpent  = new ArrayList<>();
        List<BigDecimal> weeklyLiters = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i < weeklyRows.size()) {
                weeklySpent.add(toBigDecimal(weeklyRows.get(i)[1]));
                weeklyLiters.add(toBigDecimal(weeklyRows.get(i)[2]));
            } else {
                weeklySpent.add(BigDecimal.ZERO);
                weeklyLiters.add(BigDecimal.ZERO);
            }
        }

        // Itens individuais para a tabela
        List<FuelSupplyItemDTO> fuelSupplyItems = fuelSupplies.stream()
                .map(this::toItemDTO).collect(Collectors.toList());

        // Histórico de trocas de óleo
        List<OilChangeItemDTO> oilChangeItems = oilMaintenances.stream()
                .map(this::toOilChangeItemDTO).collect(Collectors.toList());

        // Rankings
        List<UserRankingDTO> topUsers = buildUserRanking(
                fuelSupplyRepository.findUserRanking(startDate));
        List<StationRankingDTO> topStations = buildStationRanking(
                fuelSupplyRepository.findTopStations(startDate));
        List<FuelTypeDistributionDTO> fuelDist = buildFuelDistribution(
                fuelSupplyRepository.findFuelTypeDistribution(startDate));

        return new FuelSupplyReportDTO(
                totalSpent,
                totalLiters,
                fuelSupplies.size(),
                oilMaintenances.size(),
                overdueCount,
                BigDecimal.valueOf(avgConsumption).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(avgCostPerKm).setScale(2, RoundingMode.HALF_UP),
                weeklySpent,
                weeklyLiters,
                fuelSupplyItems,
                vehicleConsumption,
                oilChangeItems,
                topUsers,
                topStations,
                fuelDist
        );
    }

    // ── GET /relatorios/abastecimento/busca ──────────────────────────────────

    public FuelSupplySearchDTO search(String type, String date,
                                      String from, String to,
                                      String plate, String recordType) {
        List<FuelSupply> fuelSupplies = new ArrayList<>();

        // Filtra por tipo de busca
        switch (type) {
            case "data" -> {
                LocalDateTime start = LocalDateTime.parse(date + "T00:00:00");
                LocalDateTime end   = LocalDateTime.parse(date + "T23:59:59");
                fuelSupplies = fuelSupplyRepository
                        .findByDateTimeBetweenOrderByDateTimeDesc(start, end);
            }
            case "intervalo" -> {
                LocalDateTime start = LocalDateTime.parse(from + "T00:00:00");
                LocalDateTime end   = LocalDateTime.parse(to + "T23:59:59");
                fuelSupplies = fuelSupplyRepository
                        .findByDateTimeBetweenOrderByDateTimeDesc(start, end);
            }
            case "veiculo" -> fuelSupplies =
                    fuelSupplyRepository.findByExitRecordVehiclePlateOrderByDateTimeDesc(plate);
        }

        List<FuelSupplyItemDTO> fuelSupplyItems = List.of();
        List<OilChangeItemDTO> oilChangeItems = List.of();

        // Filtra por tipo de registro
        if ("ambos".equals(recordType) || "abast".equals(recordType)) {
            fuelSupplyItems = fuelSupplies.stream()
                    .map(this::toItemDTO).collect(Collectors.toList());
        }
        if ("ambos".equals(recordType) || "oleo".equals(recordType)) {
            oilChangeItems = oilMaintenanceRepository
                    .findByCreatedAtAfterOrderByCreatedAtDesc(resolveStartDate("30"))
                    .stream().map(this::toOilChangeItemDTO).collect(Collectors.toList());
        }

        List<VehicleConsumptionDTO> vehicles = buildVehicleConsumption(
                fuelSupplyRepository.findConsumptionByVehicle(resolveStartDate("30")),
                vehicleRepository.findAll());

        return new FuelSupplySearchDTO(fuelSupplyItems, oilChangeItems, vehicles);
    }

    // ── Métodos auxiliares ───────────────────────────────────────────────────

    private LocalDateTime resolveStartDate(String period) {
        return switch (period) {
            case "hoje" -> LocalDateTime.now().toLocalDate().atStartOfDay();
            case "7"    -> LocalDateTime.now().minusDays(7);
            case "30"   -> LocalDateTime.now().minusDays(30);
            case "ano"  -> LocalDateTime.now().minusYears(1);
            default     -> LocalDateTime.now().minusDays(30);
        };
    }

    private FuelSupplyItemDTO toItemDTO(FuelSupply f) {
        RegistroSaida exitRecord = f.getExitRecord();
        Vehicle vehicle = exitRecord != null ? exitRecord.getVehicle() : null;
        String responsible = exitRecord != null && exitRecord.getUsuario() != null
                ? exitRecord.getUsuario().getNome() : null;

        return new FuelSupplyItemDTO(
                f.getDateTime(),
                vehicle != null ? vehicle.getPrefixo() : null,
                responsible,
                f.getFuelType(),
                f.getLitersAmount(),
                f.getTotalValue(),
                f.getOdometerReading(),
                f.getStationName(),
                f.getStationCity(),
                f.getInvoiceNumber()
        );
    }

    private OilChangeItemDTO toOilChangeItemDTO(OilMaintenance o) {
        Vehicle vehicle = o.getExitRecord() != null
                ? o.getExitRecord().getVehicle() : null;

        return new OilChangeItemDTO(
                o.getCreatedAt(),
                vehicle != null ? vehicle.getPlaca() : null,
                o.getOdometerAtChange(),
                o.getNextChangeOdometer(),
                vehicle != null ? vehicle.getKmAtual() : null
        );
    }

    private List<VehicleConsumptionDTO> buildVehicleConsumption(
            List<Object[]> rows, List<Vehicle> allVehicles) {
        return rows.stream().map(row -> {
            String plate        = (String) row[0];
            BigDecimal liters   = toBigDecimal(row[1]);
            BigDecimal totalKm  = toBigDecimal(row[2]);
            double consumptionKmL = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
            BigDecimal totalSpent = toBigDecimal(row[4]);

            double costPerKm = (totalKm != null && totalKm.compareTo(BigDecimal.ZERO) > 0)
                    ? totalSpent.divide(totalKm, 4, RoundingMode.HALF_UP).doubleValue() : 0;

            Vehicle vehicle = allVehicles.stream()
                    .filter(v -> v.getPlaca().equals(plate))
                    .findFirst().orElse(null);

            var latestMaintenance = vehicle != null
                    ? oilMaintenanceRepository.findLatestByVehicleId(vehicle.getIdVeiculo())
                    : java.util.Optional.empty();

            return new VehicleConsumptionDTO(
                    plate,
                    vehicle != null ? vehicle.getKmAtual() : null,
                    latestMaintenance.map(OilMaintenance::getNextChangeOdometer).orElse(null),
                    latestMaintenance.map(OilMaintenance::getOdometerAtChange).orElse(null),
                    latestMaintenance.map(o -> o.getCreatedAt().toLocalDate().toString()).orElse(null),
                    consumptionKmL,
                    costPerKm,
                    totalSpent,
                    liters
            );
        }).collect(Collectors.toList());
    }

    private List<UserRankingDTO> buildUserRanking(List<Object[]> rows) {
        return rows.stream().map(row -> new UserRankingDTO(
                (String) row[0],
                ((Number) row[1]).intValue(),
                toBigDecimal(row[2])
        )).collect(Collectors.toList());
    }

    private List<StationRankingDTO> buildStationRanking(List<Object[]> rows) {
        return rows.stream().map(row -> new StationRankingDTO(
                (String) row[0],
                (String) row[1],
                ((Number) row[2]).longValue()
        )).collect(Collectors.toList());
    }

    private List<FuelTypeDistributionDTO> buildFuelDistribution(List<Object[]> rows) {
        return rows.stream().map(row -> new FuelTypeDistributionDTO(
                (String) row[0],
                row[1] != null ? ((Number) row[1]).doubleValue() : 0.0
        )).collect(Collectors.toList());
    }

    private BigDecimal toBigDecimal(Object value) {
        return value != null ? new BigDecimal(value.toString()) : BigDecimal.ZERO;
    }
}