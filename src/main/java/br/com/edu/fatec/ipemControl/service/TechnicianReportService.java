package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.*;
import br.com.edu.fatec.ipemControl.entity.User;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TechnicianReportService {

    private static final DateTimeFormatter FMT_DATE     = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final DepartureLogRepository departureLogRepository;
    private final FuelingRepository fuelingRepository;
    private final UserRepository userRepository;
    private final OilChangeRepository oilChangeRepository;
    private final UserDocumentRepository userDocumentRepository;

    private LocalDateTime startDate(String period) {
        return switch (period) {
            case "hoje" -> LocalDate.now().atStartOfDay();
            case "7"    -> LocalDateTime.now().minusDays(7);
            case "30"   -> LocalDateTime.now().minusDays(30);
            case "year" -> LocalDateTime.now().minusYears(1);
            default     -> LocalDate.now().atStartOfDay();
        };
    }

    private double numWeeks(String period) {
        return switch (period) {
            case "hoje" -> 1.0 / 7;
            case "7"    -> 1.0;
            case "30"   -> 4.0;
            case "year" -> 52.0;
            default     -> 1.0;
        };
    }

    private BigDecimal safe(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    // ── GET /reports/technicians/general ─────────────────────────
    public GeneralReportDTO generateSummary(String period) {
        LocalDateTime start = startDate(period);

        List<Object[]> departuresKm = departureLogRepository.buscarSaidasKmPorTecnico(start);

        List<Object[]> costs = fuelingRepository.findCostByTechnician(start);
        Map<Integer, BigDecimal> costMap = new HashMap<>();
        for (Object[] row : costs) {
            Integer reg = ((Number) row[0]).intValue();
            BigDecimal val = row[1] instanceof BigDecimal bd ? bd
                    : BigDecimal.valueOf(((Number) row[1]).doubleValue());
            costMap.put(reg, val);
        }

        List<TechnicianSummaryDTO> technicians   = new ArrayList<>();
        List<Long>       departuresPerTech = new ArrayList<>();
        List<BigDecimal> kmPerTech         = new ArrayList<>();
        List<BigDecimal> spendingPerTech   = new ArrayList<>();

        long       totalDepartures = 0;
        BigDecimal totalKm         = BigDecimal.ZERO;
        BigDecimal totalCost       = BigDecimal.ZERO;

        for (Object[] row : departuresKm) {
            Integer reg      = ((Number) row[0]).intValue();
            String  name     = (String) row[1];
            long    deps     = ((Number) row[2]).longValue();
            BigDecimal km    = row[3] instanceof BigDecimal bd ? bd
                    : BigDecimal.valueOf(((Number) row[3]).doubleValue());
            BigDecimal spend = costMap.getOrDefault(reg, BigDecimal.ZERO);

            technicians.add(new TechnicianSummaryDTO(reg, name, deps, km));
            departuresPerTech.add(deps);
            kmPerTech.add(km);
            spendingPerTech.add(spend);

            totalDepartures += deps;
            totalKm          = totalKm.add(km);
            totalCost        = totalCost.add(spend);
        }

        List<Object[]>   weekRaw   = departureLogRepository.buscarKmPorSemana(start);
        List<BigDecimal> kmPerWeek = new ArrayList<>();
        for (Object[] row : weekRaw) {
            BigDecimal val = row[1] != null
                    ? BigDecimal.valueOf(((Number) row[1]).doubleValue())
                    .setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            kmPerWeek.add(val);
        }
        while (kmPerWeek.size() < 4) kmPerWeek.add(BigDecimal.ZERO);
        Collections.reverse(kmPerWeek);

        GeneralReportDTO.GeneralSummary summary = new GeneralReportDTO.GeneralSummary();
        summary.setTotalDepartures(totalDepartures);
        summary.setActiveTechnicians(departureLogRepository.countActiveTechnicians());
        summary.setTotalKm(totalKm);
        summary.setTotalCost(totalCost);
        summary.setDeparturesPerTechnician(departuresPerTech);
        summary.setKmPerTechnician(kmPerTech);
        summary.setSpendingPerTechnician(spendingPerTech);
        summary.setKmPerWeek(kmPerWeek);

        GeneralReportDTO dto = new GeneralReportDTO();
        dto.setSummary(summary);
        dto.setTechnicians(technicians);
        return dto;
    }

    // ── GET /reports/technicians/{registration} ───────────────────
    public TechnicianReportDTO generateIndividualReport(Integer registration, String period) {

        User user = userRepository.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico não encontrado."));

        LocalDateTime start = startDate(period);
        TechnicianReportDTO dto = new TechnicianReportDTO();

        // Identificação
        dto.setRegistration(user.getRegistration());
        dto.setName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setUserType(user.getUserType());
        dto.setLicenseType(user.getLicenseType());
        dto.setLicenseNumber(user.getDriverLicenseNumber());
        dto.setCpf(user.getCpf());
        dto.setEmail(user.getEmail());
        dto.setBirthDate(user.getBirthDate() != null
                ? user.getBirthDate().format(FMT_DATE) : null);

        // Status operacional
        dto.setActive(user.isActiveEmployee());
        dto.setRegistrationDate(user.getCreatedAt() != null
                ? user.getCreatedAt().format(FMT_DATETIME) : null);
        dto.setLastUpdate(user.getUpdatedAt() != null
                ? user.getUpdatedAt().format(FMT_DATETIME) : null);

        var openDeparture = departureLogRepository
                .findTopByUserRegistrationAndStatusOrderByDepartureDatetimeDesc(
                        registration, "in_progress");
        dto.setOpenDeparture(openDeparture.isPresent());
        dto.setOpenDepartureId(openDeparture.map(d -> d.getId()).orElse(null));

        // Uso por período
        List<String> periods = List.of("hoje", "7", "30", "year");
        Map<String, Long>       departuresMap = new LinkedHashMap<>();
        Map<String, BigDecimal> kmMap         = new LinkedHashMap<>();
        for (String p : periods) {
            LocalDateTime ini = startDate(p);
            departuresMap.put(p, departureLogRepository.countPorMatriculaEPeriodo(registration, ini));
            kmMap.put(p, safe(departureLogRepository.sumKmPorMatriculaEPeriodo(registration, ini)));
        }
        dto.setDeparturesByPeriod(departuresMap);
        dto.setKmByPeriod(kmMap);

        departureLogRepository
                .findTopByUserRegistrationOrderByDepartureDatetimeDesc(registration)
                .ifPresent(d -> {
                    dto.setLastDepartureDate(d.getDepartureDatetime() != null
                            ? d.getDepartureDatetime().format(FMT_DATETIME) : null);
                    dto.setLastDepartureVehicle(d.getVehicle() != null
                            ? d.getVehicle().getPrefix() + " — " + d.getVehicle().getLicensePlate() : null);
                    dto.setLastDepartureDestination(d.getDestination());
                });

        // Comportamento operacional
        dto.setAvgDurationHours(departureLogRepository.calcularTempoMedioHoras(registration, start));
        dto.setLongestKm(safe(departureLogRepository.buscarMaiorKm(registration, start)));
        dto.setLongestDurationHours(departureLogRepository.buscarMaiorDuracaoHoras(registration, start));
        long depsInPeriod = departuresMap.getOrDefault(period, 0L);
        dto.setDepartureFrequencyPerWeek(depsInPeriod / numWeeks(period));

        // Financeiro por período
        Map<String, BigDecimal> spendingMap = new LinkedHashMap<>();
        Map<String, Long>       fuelingsMap = new LinkedHashMap<>();
        for (String p : periods) {
            LocalDateTime ini = startDate(p);
            spendingMap.put(p, safe(fuelingRepository.sumSpendingByRegistrationAndPeriod(registration, ini)));
            fuelingsMap.put(p, fuelingRepository.countByRegistrationAndPeriod(registration, ini));
        }
        dto.setSpendingByPeriod(spendingMap);
        dto.setFuelingsByPeriod(fuelingsMap);

        // Manutenção
        dto.setOilChanges(oilChangeRepository.countByDepartureLogUserRegistration(registration));
        oilChangeRepository
                .findTopByDepartureLogUserRegistrationOrderByCreatedAtDesc(registration)
                .ifPresent(o -> dto.setLastOilChange(
                        o.getCreatedAt() != null ? o.getCreatedAt().format(FMT_DATETIME) : null));
        dto.setUsedVehicles(departureLogRepository.buscarVeiculosUtilizados(registration));

        // Documentos
        TechnicianReportDTO.DocumentsDTO documents = new TechnicianReportDTO.DocumentsDTO();
        documents.setReceived(userDocumentRepository.countByUserRegistration(registration));
        documents.setRead(userDocumentRepository.countByUserRegistrationAndReadTrue(registration));
        documents.setDownloaded(userDocumentRepository.countByUserRegistrationAndDownloadedTrue(registration));
        dto.setDocuments(documents);

        // Destinos top 5
        List<FrequentDestinationDTO> destinations = new ArrayList<>();
        for (Object[] row : departureLogRepository.buscarDestinosMaisFrequentes(registration))
            destinations.add(new FrequentDestinationDTO((String) row[0], ((Number) row[1]).longValue()));
        dto.setDestinations(destinations);

        // Serviços por tipo
        Map<String, Long> services = new LinkedHashMap<>();
        for (Object[] row : departureLogRepository.buscarServicosDoTecnico(registration, start))
            services.put(row[0] != null ? (String) row[0] : "Outros", ((Number) row[1]).longValue());
        dto.setServices(services);

        return dto;
    }
}