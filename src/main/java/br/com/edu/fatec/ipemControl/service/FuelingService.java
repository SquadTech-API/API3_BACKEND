package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.*;
import br.com.edu.fatec.ipemControl.entities.DepartureLog;
import br.com.edu.fatec.ipemControl.entities.Fueling;
import br.com.edu.fatec.ipemControl.entities.OilChange;
import br.com.edu.fatec.ipemControl.entities.Vehicle;
import br.com.edu.fatec.ipemControl.repository.RefuelingRepository;
import br.com.edu.fatec.ipemControl.repository.ExitRecordRepository;
import br.com.edu.fatec.ipemControl.repository.OilChangeRepository;
import br.com.edu.fatec.ipemControl.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FuelingService {

    private final RefuelingRepository refuelingRepository;
    private final OilChangeRepository oilChangeRepository;
    private final ExitRecordRepository exitRecordRepository;
    private final VehicleRepository vehicleRepository;

    public FuelingService(
            RefuelingRepository refuelingRepository,
            OilChangeRepository oilChangeRepository,
            ExitRecordRepository exitRecordRepository,
            VehicleRepository vehicleRepository) {
        this.refuelingRepository = refuelingRepository;
        this.oilChangeRepository = oilChangeRepository;
        this.exitRecordRepository = exitRecordRepository;
        this.vehicleRepository = vehicleRepository;
    }

    // ── POST /abastecimento ──────────────────────────────────────────────────

    public SavedFuelingDTO save(FuelingDTO dto) {
        DepartureLog departureLog = exitRecordRepository.findById(dto.getTripId())
                .orElseThrow(() -> new RuntimeException("Registro from saída não encontrado."));

        Fueling fueling = new Fueling();
        fueling.setDepartureLog(departureLog);
        fueling.setDateTime(dto.getDateTime());
        fueling.setFuelType(dto.getFuelType());
        fueling.setLitersAmount(dto.getLitersQuantity());
        fueling.setTotalValue(dto.getTotalAmount());
        fueling.setFuelingKm(dto.getFuelingMileage());
        fueling.setGasStationName(dto.getGasStationName());
        fueling.setGasStationCity(dto.getGasStationCity());
        fueling.setReceipt(dto.getInvoiceNumber());

        Fueling saved = refuelingRepository.save(fueling);

        return new SavedFuelingDTO(
                saved.getFuelingId(),
                saved.getDateTime(),
                saved.getFuelType(),
                saved.getLitersAmount(),
                saved.getTotalValue(),
                saved.getFuelingKm(),
                saved.getGasStationName(),
                saved.getGasStationCity(),
                saved.getReceipt(),
                departureLog.getDepartureLogId()
        );
    }

    // ── GET /abastecimento/historico ─────────────────────────────────────────

    public List<FuelingHistoryDTO> findHistory(Integer vehicleId) {
        List<Fueling> lista = (vehicleId != null)
                ? refuelingRepository.findByDepartureLogVehicleVehicleIdOrderByDateTimeDesc(vehicleId)
                : refuelingRepository.findAllByOrderByDateTimeDesc();
        return lista.stream().map(this::toHistoryDTO).collect(Collectors.toList());
    }

    private FuelingHistoryDTO toHistoryDTO(Fueling a) {
        DepartureLog rs    = a.getDepartureLog();
        Vehicle vehicle = rs != null ? rs.getVehicle() : null;
        String responsible  = rs != null && rs.getUser() != null ? rs.getUser().getName() : null;

        return new FuelingHistoryDTO(
                a.getFuelingId(), a.getDateTime(), a.getFuelType(),
                a.getLitersAmount(), a.getTotalValue(), a.getFuelingKm(),
                a.getGasStationName(), a.getGasStationCity(), a.getReceipt(),
                vehicle != null ? vehicle.getVehicleId() : null,
                vehicle != null ? vehicle.getModel()    : null,
                vehicle != null ? vehicle.getPrefix()   : null,
                vehicle != null ? vehicle.getLicensePlate()     : null,
                responsible
        );
    }

    // ── GET /relatorios/abastecimento/geral ──────────────────────────────────

    public FuelReportDTO generateReport(String period) {
        LocalDateTime startDate = resolveStartDate(period);

        List<Fueling> fuelings = refuelingRepository
                .findByDateTimeAfterOrderByDateTimeDesc(startDate);

        BigDecimal totalSpent = fuelings.stream()
                .map(a -> a.getTotalValue() != null ? a.getTotalValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLiters = fuelings.stream()
                .map(a -> a.getLitersAmount() != null ? a.getLitersAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<OilChange> oilChanges = oilChangeRepository
                .findByCreatedAtAfterOrderByCreatedAtDesc(startDate);

        List<Vehicle> allVehicles = vehicleRepository.findAll();
        int overdueMaintenanceCount = 0;
        for (Vehicle v : allVehicles) {
            Optional<OilChange> latestOilChange = oilChangeRepository.findLatestByVehicle(v.getVehicleId());
            if (latestOilChange.isPresent() && v.getCurrentKm() != null &&
                    v.getCurrentKm().compareTo(latestOilChange.get().getNextChangeKm()) >= 0) {
                overdueMaintenanceCount++;
            }
        }

        List<Object[]> consumptionRows = refuelingRepository.findVehicleConsumption(startDate);
        List<VehicleConsumptionDTO> vehicleConsumption = buildVehicleConsumption(consumptionRows, allVehicles);

        double averageConsumption = vehicleConsumption.stream()
                .filter(v -> v.getFuelConsumptionKmPerLiter() != null).mapToDouble(VehicleConsumptionDTO::getFuelConsumptionKmPerLiter)
                .average().orElse(0);
        double averageCost = vehicleConsumption.stream()
                .filter(v -> v.getCostPerKilometer() != null).mapToDouble(VehicleConsumptionDTO::getCostPerKilometer)
                .average().orElse(0);

        List<Object[]> weeklyRows = refuelingRepository.findWeeklyStatistics(startDate);
        List<BigDecimal> weeklySpending  = new ArrayList<>();
        List<BigDecimal> weeklyLiters = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i < weeklyRows.size()) {
                weeklySpending.add(toBigDecimal(weeklyRows.get(i)[1]));
                weeklyLiters.add(toBigDecimal(weeklyRows.get(i)[2]));
            } else {
                weeklySpending.add(BigDecimal.ZERO);
                weeklyLiters.add(BigDecimal.ZERO);
            }
        }

        List<FuelingItemDTO> fuelingItems = fuelings.stream()
                .map(this::toItemDTO).collect(Collectors.toList());

        // CORRIGIDO: usa getVehicle() direto da entidade OilChange
        List<OilChangeItemDTO> oilChangeItems = oilChanges.stream()
                .map(this::toOilChangeItemDTO).collect(Collectors.toList());

        List<UserRankingDTO>         userRankings  = buildUserRankings(refuelingRepository.findUserRankings(startDate));
        List<StationRankingDTO>           stationRankings    = buildStationRankings(refuelingRepository.findStationRankings(startDate));
        List<FuelDistributionDTO> fuelDistribution    = buildFuelDistribution(refuelingRepository.findFuelDistribution(startDate));

        return new FuelReportDTO(
                totalSpent, totalLiters, fuelings.size(), oilChanges.size(),
                overdueMaintenanceCount,
                BigDecimal.valueOf(averageConsumption).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(averageCost).setScale(2, RoundingMode.HALF_UP),
                weeklySpending, weeklyLiters, fuelingItems,
                vehicleConsumption, oilChangeItems, userRankings, stationRankings, fuelDistribution
        );
    }

    public FuelingSearchDTO search(String type, String date, String from, String to,
                                        String licensePlate, String recordType) {
        List<Fueling> fuelings = new ArrayList<>();
        switch (type) {
            case "date" -> {
                LocalDateTime start = LocalDateTime.parse(date + "T00:00:00");
                LocalDateTime end    = LocalDateTime.parse(date + "T23:59:59");
                fuelings = refuelingRepository.findByDateTimeBetweenOrderByDateTimeDesc(start, end);
            }
            case "data" -> {
                LocalDateTime start = LocalDateTime.parse(date + "T00:00:00");
                LocalDateTime end    = LocalDateTime.parse(date + "T23:59:59");
                fuelings = refuelingRepository.findByDateTimeBetweenOrderByDateTimeDesc(start, end);
            }
            case "interval" -> {
                LocalDateTime start = LocalDateTime.parse(from + "T00:00:00");
                LocalDateTime end    = LocalDateTime.parse(to + "T23:59:59");
                fuelings = refuelingRepository.findByDateTimeBetweenOrderByDateTimeDesc(start, end);
            }
            case "vehicle" -> fuelings =
                    refuelingRepository.findByDepartureLogVehicleLicensePlateOrderByDateTimeDesc(licensePlate);
        }

        List<FuelingItemDTO> fuelingItems = List.of();
        List<OilChangeItemDTO>     oilChangeItems     = List.of();

        if ("both".equals(recordType) || "ambos".equals(recordType)
                || "fueling".equals(recordType) || "abast".equals(recordType)) {
            fuelingItems = fuelings.stream().map(this::toItemDTO).collect(Collectors.toList());
        }
        if ("both".equals(recordType) || "ambos".equals(recordType)
                || "oil".equals(recordType) || "oleo".equals(recordType)) {
            oilChangeItems = oilChangeRepository
                    .findByCreatedAtAfterOrderByCreatedAtDesc(resolveStartDate("30"))
                    .stream().map(this::toOilChangeItemDTO).collect(Collectors.toList());
        }

        List<VehicleConsumptionDTO> vehicles = buildVehicleConsumption(
                refuelingRepository.findVehicleConsumption(resolveStartDate("30")),
                vehicleRepository.findAll());

        return new FuelingSearchDTO(fuelingItems, oilChangeItems, vehicles);
    }

    // ── Auxiliares ────────────────────────────────────────────────────────────

    private LocalDateTime resolveStartDate(String period) {
        return switch (period) {
            case "hoje" -> LocalDateTime.now().toLocalDate().atStartOfDay();
            case "7"    -> LocalDateTime.now().minusDays(7);
            case "30"   -> LocalDateTime.now().minusDays(30);
            case "year"  -> LocalDateTime.now().minusYears(1);
            default     -> LocalDateTime.now().minusDays(30);
        };
    }

    private FuelingItemDTO toItemDTO(Fueling a) {
        DepartureLog rs   = a.getDepartureLog();
        Vehicle vehicle = rs != null ? rs.getVehicle() : null;
        String responsible = rs != null && rs.getUser() != null ? rs.getUser().getName() : null;
        return new FuelingItemDTO(
                a.getDateTime(),
                vehicle != null ? vehicle.getPrefix() : null,
                responsible, a.getFuelType(), a.getLitersAmount(),
                a.getTotalValue(), a.getFuelingKm(), a.getGasStationName(),
                a.getGasStationCity(), a.getReceipt()
        );
    }

    /**
     * CORRIGIDO: agora resolve o Vehicle pelo campo direto t.getVehicle()
     * em vez from t.getDepartureLog().getVehicle() (que falha quando registroSaida é null
     * em trocas avulsas não vinculadas a uma saída).
     *
     * CORRIGIDO: OilChangeItemDTO espera LocalDateTime — usa createdAt (timestamp do departureLog)
     * como aproximação aceitável enquanto oilChangeDate (LocalDate) não é adicionado ao DTO.
     */
    private OilChangeItemDTO toOilChangeItemDTO(OilChange t) {
        // Usa o vínculo direto com Vehicle adicionado na entidade corrigida
        Vehicle vehicle = t.getVehicle();

        // Se por algum motivo o vínculo direto for null, tenta via registroSaida
        if (vehicle == null && t.getDepartureLog() != null) {
            vehicle = t.getDepartureLog().getVehicle();
        }

        return new OilChangeItemDTO(
                t.getCreatedAt(),                                        // LocalDateTime — timestamp
                vehicle != null ? vehicle.getLicensePlate()     : null,
                t.getChangeKm(),
                t.getNextChangeKm(),
                vehicle != null ? vehicle.getCurrentKm()   : null
        );
    }

    private List<VehicleConsumptionDTO> buildVehicleConsumption(List<Object[]> rows, List<Vehicle> allVehicles) {
        return rows.stream().map(row -> {
            String licensePlate          = (String) row[0];
            BigDecimal liters     = toBigDecimal(row[1]);
            BigDecimal totalMileage    = toBigDecimal(row[2]);
            double consumptionKmPerLiter     = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
            BigDecimal totalSpent = toBigDecimal(row[4]);

            double costPerKilometer = (totalMileage != null && totalMileage.compareTo(BigDecimal.ZERO) > 0)
                    ? totalSpent.divide(totalMileage, 4, RoundingMode.HALF_UP).doubleValue() : 0;

            Vehicle vehicle = allVehicles.stream()
                    .filter(v -> v.getLicensePlate().equals(licensePlate)).findFirst().orElse(null);

            Optional<OilChange> latestOilChange = vehicle != null
                    ? oilChangeRepository.findLatestByVehicle(vehicle.getVehicleId())
                    : Optional.empty();

            return new VehicleConsumptionDTO(
                    licensePlate,
                    vehicle != null ? vehicle.getCurrentKm() : null,
                    latestOilChange.map(OilChange::getNextChangeKm).orElse(null),
                    latestOilChange.map(OilChange::getChangeKm).orElse(null),
                    latestOilChange.map(t -> t.getCreatedAt().toLocalDate().toString()).orElse(null),
                    consumptionKmPerLiter, costPerKilometer, totalSpent, liters
            );
        }).collect(Collectors.toList());
    }

    private List<UserRankingDTO> buildUserRankings(List<Object[]> rows) {
        return rows.stream().map(l -> new UserRankingDTO(
                (String) l[0], ((Number) l[1]).intValue(), toBigDecimal(l[2])
        )).collect(Collectors.toList());
    }

    private List<StationRankingDTO> buildStationRankings(List<Object[]> rows) {
        return rows.stream().map(l -> new StationRankingDTO(
                (String) l[0], (String) l[1], ((Number) l[2]).longValue()
        )).collect(Collectors.toList());
    }

    private List<FuelDistributionDTO> buildFuelDistribution(List<Object[]> rows) {
        long total = rows.stream().mapToLong(l -> ((Number) l[1]).longValue()).sum();
        return rows.stream().map(l -> {
            long q  = ((Number) l[1]).longValue();
            double pct = total > 0 ? Math.round((q * 100.0 / total) * 10.0) / 10.0 : 0.0;
            return new FuelDistributionDTO((String) l[0], pct);
        }).collect(Collectors.toList());
    }

    private BigDecimal toBigDecimal(Object v) {
        return v != null ? new BigDecimal(v.toString()) : BigDecimal.ZERO;
    }
}
