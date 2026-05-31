package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.*;
import br.com.edu.fatec.ipemControl.entity.*;
import br.com.edu.fatec.ipemControl.exception.BusinessRuleException;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuelingService {

    private final FuelingRepository fuelingRepository;
    private final OilChangeRepository oilChangeRepository;
    private final DepartureLogRepository departureLogRepository;
    private final VehicleRepository vehicleRepository;
    private final FuelTypeRepository fuelTypeRepository;

    // ── POST /fuelings ────────────────────────────────────────────
    public SavedFuelingDTO save(FuelingDTO dto) {
        DepartureLog departureLog = departureLogRepository.findById(dto.getDepartureLogId())
                .orElseThrow(() -> new ResourceNotFoundException("Saída não encontrada."));

        FuelType fuelType = fuelTypeRepository.findById(dto.getFuelTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de combustível não encontrado."));

        if (dto.getLiters() == null || dto.getLiters().compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessRuleException("Quantidade de litros inválida.");
        if (dto.getTotalValue() == null || dto.getTotalValue().compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessRuleException("Valor total inválido.");

        Fueling fueling = new Fueling();
        fueling.setDepartureLog(departureLog);
        fueling.setFuelType(fuelType);
        fueling.setFuelingDatetime(dto.getFuelingDatetime());
        fueling.setLiters(dto.getLiters());
        fueling.setTotalValue(dto.getTotalValue());
        fueling.setMileageAtFueling(dto.getMileageAtFueling());
        fueling.setStationName(dto.getStationName());
        fueling.setStationCity(dto.getStationCity());
        fueling.setInvoiceNumber(dto.getInvoiceNumber());
        fueling.setReceiptUrl(dto.getReceiptUrl());

        return toSavedDTO(fuelingRepository.save(fueling));
    }

    // ── GET /fuelings/history?vehicleId={id} ─────────────────────
    public List<FuelingHistoryDTO> findHistory(Integer vehicleId) {
        List<Fueling> list = (vehicleId != null)
                ? fuelingRepository.findByDepartureLogVehicleIdOrderByFuelingDatetimeDesc(vehicleId)
                : fuelingRepository.findAllByOrderByFuelingDatetimeDesc();
        return list.stream().map(this::toHistoryDTO).collect(Collectors.toList());
    }

    // ── GET /reports/fueling?period={period} ──────────────────────
    public FuelReportDTO generateReport(String period) {
        LocalDateTime startDate = resolveStartDate(period);

        List<Fueling> fuelings = fuelingRepository
                .findByFuelingDatetimeAfterOrderByFuelingDatetimeDesc(startDate);

        BigDecimal totalSpent = fuelings.stream()
                .map(f -> f.getTotalValue() != null ? f.getTotalValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLiters = fuelings.stream()
                .map(f -> f.getLiters() != null ? f.getLiters() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<OilChange> oilChanges = oilChangeRepository
                .findByCreatedAtAfterOrderByCreatedAtDesc(startDate);

        List<Vehicle> allVehicles = vehicleRepository.findAll();
        int overdueCount = 0;
        for (Vehicle v : allVehicles) {
            Optional<OilChange> latest = oilChangeRepository.findLatestByVehicle(v.getId());
            if (latest.isPresent() && v.getCurrentMileage() != null &&
                    v.getCurrentMileage().compareTo(latest.get().getNextChangeMileage()) >= 0)
                overdueCount++;
        }

        List<VehicleConsumptionDTO> vehicleConsumption =
                buildVehicleConsumption(fuelingRepository.findVehicleConsumption(startDate), allVehicles);

        double avgConsumption = vehicleConsumption.stream()
                .filter(v -> v.getFuelConsumptionKmPerLiter() != null)
                .mapToDouble(VehicleConsumptionDTO::getFuelConsumptionKmPerLiter)
                .average().orElse(0);
        double avgCost = vehicleConsumption.stream()
                .filter(v -> v.getCostPerKilometer() != null)
                .mapToDouble(VehicleConsumptionDTO::getCostPerKilometer)
                .average().orElse(0);

        List<Object[]> weeklyRows = fuelingRepository.findWeeklyStatistics(startDate);
        List<BigDecimal> weeklySpending = new ArrayList<>();
        List<BigDecimal> weeklyLiters  = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i < weeklyRows.size()) {
                weeklySpending.add(toBd(weeklyRows.get(i)[1]));
                weeklyLiters.add(toBd(weeklyRows.get(i)[2]));
            } else {
                weeklySpending.add(BigDecimal.ZERO);
                weeklyLiters.add(BigDecimal.ZERO);
            }
        }

        return new FuelReportDTO(
                totalSpent, totalLiters, fuelings.size(), oilChanges.size(), overdueCount,
                BigDecimal.valueOf(avgConsumption).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(avgCost).setScale(2, RoundingMode.HALF_UP),
                weeklySpending, weeklyLiters,
                fuelings.stream().map(this::toItemDTO).collect(Collectors.toList()),
                vehicleConsumption,
                oilChanges.stream().map(this::toOilChangeItemDTO).collect(Collectors.toList()),
                buildUserRankings(fuelingRepository.findUserRankings(startDate)),
                buildStationRankings(fuelingRepository.findStationRankings(startDate)),
                buildFuelDistribution(fuelingRepository.findFuelDistribution(startDate))
        );
    }

    // ── GET /fuelings/search ──────────────────────────────────────
    public FuelingSearchDTO search(String type, String date, String from, String to,
                                   String licensePlate, String recordType) {
        List<Fueling> fuelings = new ArrayList<>();
        switch (type) {
            case "date", "data" -> {
                LocalDateTime start = LocalDateTime.parse(date + "T00:00:00");
                LocalDateTime end   = LocalDateTime.parse(date + "T23:59:59");
                fuelings = fuelingRepository.findByFuelingDatetimeBetweenOrderByFuelingDatetimeDesc(start, end);
            }
            case "interval" -> {
                LocalDateTime start = LocalDateTime.parse(from + "T00:00:00");
                LocalDateTime end   = LocalDateTime.parse(to + "T23:59:59");
                fuelings = fuelingRepository.findByFuelingDatetimeBetweenOrderByFuelingDatetimeDesc(start, end);
            }
            case "vehicle" -> fuelings =
                    fuelingRepository.findByDepartureLogVehicleLicensePlateOrderByFuelingDatetimeDesc(licensePlate);
        }

        List<FuelingItemDTO>   fuelingItems   = List.of();
        List<OilChangeItemDTO> oilChangeItems = List.of();

        if ("both".equals(recordType) || "fueling".equals(recordType))
            fuelingItems = fuelings.stream().map(this::toItemDTO).collect(Collectors.toList());
        if ("both".equals(recordType) || "oil".equals(recordType))
            oilChangeItems = oilChangeRepository
                    .findByCreatedAtAfterOrderByCreatedAtDesc(resolveStartDate("30"))
                    .stream().map(this::toOilChangeItemDTO).collect(Collectors.toList());

        return new FuelingSearchDTO(fuelingItems, oilChangeItems,
                buildVehicleConsumption(
                        fuelingRepository.findVehicleConsumption(resolveStartDate("30")),
                        vehicleRepository.findAll()));
    }

    // ── Helpers ───────────────────────────────────────────────────
    private LocalDateTime resolveStartDate(String period) {
        return switch (period) {
            case "hoje" -> LocalDateTime.now().toLocalDate().atStartOfDay();
            case "7"    -> LocalDateTime.now().minusDays(7);
            case "30"   -> LocalDateTime.now().minusDays(30);
            case "year" -> LocalDateTime.now().minusYears(1);
            default     -> LocalDateTime.now().minusDays(30);
        };
    }

    private SavedFuelingDTO toSavedDTO(Fueling f) {
        return new SavedFuelingDTO(
                f.getId(), f.getFuelingDatetime(),
                f.getFuelType() != null ? f.getFuelType().getName() : null,
                f.getLiters(), f.getTotalValue(), f.getMileageAtFueling(),
                f.getStationName(), f.getStationCity(), f.getInvoiceNumber(),
                f.getDepartureLog() != null ? f.getDepartureLog().getId() : null
        );
    }

    private FuelingHistoryDTO toHistoryDTO(Fueling f) {
        DepartureLog dl  = f.getDepartureLog();
        Vehicle v        = dl != null ? dl.getVehicle() : null;
        String responsible = dl != null && dl.getUser() != null ? dl.getUser().getFullName() : null;
        return new FuelingHistoryDTO(
                f.getId(), f.getFuelingDatetime(),
                f.getFuelType() != null ? f.getFuelType().getName() : null,
                f.getLiters(), f.getTotalValue(), f.getMileageAtFueling(),
                f.getStationName(), f.getStationCity(), f.getInvoiceNumber(),
                v != null ? v.getId() : null,
                v != null ? v.getModel() : null,
                v != null ? v.getPrefix() : null,
                v != null ? v.getLicensePlate() : null,
                responsible
        );
    }

    private FuelingItemDTO toItemDTO(Fueling f) {
        DepartureLog dl  = f.getDepartureLog();
        Vehicle v        = dl != null ? dl.getVehicle() : null;
        String responsible = dl != null && dl.getUser() != null ? dl.getUser().getFullName() : null;
        return new FuelingItemDTO(
                f.getFuelingDatetime(),
                v != null ? v.getPrefix() : null,
                responsible,
                f.getFuelType() != null ? f.getFuelType().getName() : null,
                f.getLiters(), f.getTotalValue(), f.getMileageAtFueling(),
                f.getStationName(), f.getStationCity(), f.getInvoiceNumber()
        );
    }

    private OilChangeItemDTO toOilChangeItemDTO(OilChange o) {
        Vehicle v = o.getVehicle();
        if (v == null && o.getDepartureLog() != null) v = o.getDepartureLog().getVehicle();
        return new OilChangeItemDTO(
                o.getCreatedAt(),
                v != null ? v.getLicensePlate() : null,
                o.getChangeMileage(),
                o.getNextChangeMileage(),
                v != null ? v.getCurrentMileage() : null
        );
    }

    private List<VehicleConsumptionDTO> buildVehicleConsumption(List<Object[]> rows, List<Vehicle> vehicles) {
        return rows.stream().map(row -> {
            String plate     = (String) row[0];
            BigDecimal liters = toBd(row[1]);
            BigDecimal km     = toBd(row[2]);
            double kmPerLiter = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
            BigDecimal spent  = toBd(row[4]);
            double costPerKm  = km.compareTo(BigDecimal.ZERO) > 0
                    ? spent.divide(km, 4, RoundingMode.HALF_UP).doubleValue() : 0;

            Vehicle v = vehicles.stream().filter(x -> plate.equals(x.getLicensePlate())).findFirst().orElse(null);
            Optional<OilChange> latest = v != null ? oilChangeRepository.findLatestByVehicle(v.getId()) : Optional.empty();

            return new VehicleConsumptionDTO(
                    plate, v != null ? v.getCurrentMileage() : null,
                    latest.map(OilChange::getNextChangeMileage).orElse(null),
                    latest.map(OilChange::getChangeMileage).orElse(null),
                    latest.map(o -> o.getCreatedAt().toLocalDate().toString()).orElse(null),
                    kmPerLiter, costPerKm, spent, liters
            );
        }).collect(Collectors.toList());
    }

    private List<UserRankingDTO> buildUserRankings(List<Object[]> rows) {
        return rows.stream().map(r -> new UserRankingDTO(
                (String) r[0], ((Number) r[1]).intValue(), toBd(r[2]))).collect(Collectors.toList());
    }

    private List<StationRankingDTO> buildStationRankings(List<Object[]> rows) {
        return rows.stream().map(r -> new StationRankingDTO(
                (String) r[0], (String) r[1], ((Number) r[2]).longValue())).collect(Collectors.toList());
    }

    private List<FuelDistributionDTO> buildFuelDistribution(List<Object[]> rows) {
        long total = rows.stream().mapToLong(r -> ((Number) r[1]).longValue()).sum();
        return rows.stream().map(r -> {
            long q = ((Number) r[1]).longValue();
            double pct = total > 0 ? Math.round((q * 100.0 / total) * 10.0) / 10.0 : 0.0;
            return new FuelDistributionDTO((String) r[0], pct);
        }).collect(Collectors.toList());
    }

    private BigDecimal toBd(Object v) {
        return v != null ? new BigDecimal(v.toString()) : BigDecimal.ZERO;
    }
}