package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.*;
import br.com.edu.fatec.ipemControl.entity.DepartureLog;
import br.com.edu.fatec.ipemControl.entity.User;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TechnicianReportService {

    private static final DateTimeFormatter FMT_DATE     = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final ExitRecordRepository saidaRepo;
    private final RefuelingRepository abastRepo;
    private final UserRepository usuarioRepo;
    private final OilChangeRepository trocaOleoRepo;
    private final UserDocumentRepository docRepo;

    public TechnicianReportService(ExitRecordRepository saidaRepo,
                                   RefuelingRepository abastRepo,
                                   UserRepository usuarioRepo,
                                   OilChangeRepository trocaOleoRepo,
                                   UserDocumentRepository docRepo) {
        this.saidaRepo = saidaRepo;
        this.abastRepo = abastRepo;
        this.usuarioRepo = usuarioRepo;
        this.trocaOleoRepo = trocaOleoRepo;
        this.docRepo = docRepo;
    }

    private LocalDateTime dataInicio(String periodo) {
        return switch (periodo) {
            case "hoje" -> LocalDate.now().atStartOfDay();
            case "7"    -> LocalDateTime.now().minusDays(7);
            case "30"   -> LocalDateTime.now().minusDays(30);
            case "year"  -> LocalDateTime.now().minusYears(1);
            default     -> LocalDate.now().atStartOfDay();
        };
    }

    private double numeroSemanas(String periodo) {
        return switch (periodo) {
            case "hoje" -> 1.0 / 7;
            case "7"    -> 1.0;
            case "30"   -> 4.0;
            case "year"  -> 52.0;
            default     -> 1.0;
        };
    }

    private BigDecimal safe(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  GET /relatorios/technicians/geral
    // ════════════════════════════════════════════════════════════════════════

    public GeneralReportDTO generateSummary(String periodo) {
        LocalDateTime start = dataInicio(periodo);

        List<Object[]> saidasKm = saidaRepo.buscarSaidasKmPorTecnico(start);

        List<Object[]> custos = abastRepo.findCostByTechnician(start);
        Map<Integer, BigDecimal> gastoMap = new HashMap<>();
        for (Object[] row : custos) {
            Integer mat    = ((Number) row[0]).intValue();
            BigDecimal val = row[1] instanceof BigDecimal bd ? bd
                    : BigDecimal.valueOf(((Number) row[1]).doubleValue());
            gastoMap.put(mat, val);
        }

        List<TechnicianSummaryDTO>  tecnicos         = new ArrayList<>();
        List<Long>              saidasPorTecnico = new ArrayList<>();
        List<BigDecimal>        kmPorTecnico     = new ArrayList<>();
        List<BigDecimal>        gastoPorTecnico  = new ArrayList<>();

        long       totalSaidas = 0;
        BigDecimal kmTotal     = BigDecimal.ZERO;
        BigDecimal custoTotal  = BigDecimal.ZERO;

        for (Object[] row : saidasKm) {
            Integer mat    = ((Number) row[0]).intValue();
            String  nome   = (String) row[1];
            long    saidas = ((Number) row[2]).longValue();
            BigDecimal km  = row[3] instanceof BigDecimal bd ? bd
                    : BigDecimal.valueOf(((Number) row[3]).doubleValue());
            BigDecimal gasto = gastoMap.getOrDefault(mat, BigDecimal.ZERO);

            tecnicos.add(new TechnicianSummaryDTO(mat, nome, saidas, km));
            saidasPorTecnico.add(saidas);
            kmPorTecnico.add(km);
            gastoPorTecnico.add(gasto);

            totalSaidas += saidas;
            kmTotal      = kmTotal.add(km);
            custoTotal   = custoTotal.add(gasto);
        }

        List<Object[]>   semanasRaw  = saidaRepo.buscarKmPorSemana(start);
        List<BigDecimal> kmPorSemana = new ArrayList<>();
        for (Object[] row : semanasRaw) {
            BigDecimal val = row[1] != null
                    ? BigDecimal.valueOf(((Number) row[1]).doubleValue()).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            kmPorSemana.add(val);
        }
        while (kmPorSemana.size() < 4) kmPorSemana.add(BigDecimal.ZERO);
        Collections.reverse(kmPorSemana);

        GeneralReportDTO.GeneralSummary resumo = new GeneralReportDTO.GeneralSummary();
        resumo.setTotalDepartures(totalSaidas);
        resumo.setActiveTechnicians(saidaRepo.countTecnicosAtivos());
        resumo.setTotalKm(kmTotal);
        resumo.setTotalCost(custoTotal);
        resumo.setDeparturesPerTechnician(saidasPorTecnico);
        resumo.setKmPerTechnician(kmPorTecnico);
        resumo.setSpendingPerTechnician(gastoPorTecnico);
        resumo.setKmPerWeek(kmPorSemana);

        GeneralReportDTO dto = new GeneralReportDTO();
        dto.setSummary(resumo);
        dto.setTechnicians(tecnicos);
        return dto;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  GET /relatorios/technicians/{registration}
    // ════════════════════════════════════════════════════════════════════════

    public TechnicianReportDTO generateIndividualReport(Integer registration, String periodo) {

        User user = usuarioRepo.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico não encontrado."));

        LocalDateTime start = dataInicio(periodo);
        TechnicianReportDTO dto = new TechnicianReportDTO();

        // Seção 1 — Identificação
        dto.setRegistration(user.getRegistration());
        dto.setName(user.getName());
        dto.setRole(user.getPosition());
        dto.setType(user.getUserType() != null ? user.getUserType().name() : null);
        dto.setDriversLicense(user.getDriverLicenseType() != null ? user.getDriverLicenseType().name() : null);
        dto.setLicenseNumber(user.getDriverLicenseNumber());
        dto.setCpf(user.getCpf());
        dto.setEmail(user.getEmail());
        dto.setPhone(null);
        dto.setBirthDate(user.getBirthDate() != null
                ? user.getBirthDate().format(FMT_DATE) : null);

        // Seção 2 — Status operacional
        dto.setActive(user.getActiveColaborator());
        dto.setRegistrationDate(user.getCreatedAt() != null
                ? user.getCreatedAt().format(FMT_DATETIME) : null);
        dto.setLastUpdate(user.getUpdatedAt() != null
                ? user.getUpdatedAt().format(FMT_DATETIME) : null);

        Optional<DepartureLog> saidaAberta = saidaRepo
                .findTopByUserRegistrationAndStatusOrderByDateTimeDepartureDesc(registration, "em_andamento");
        dto.setOpenDeparture(saidaAberta.isPresent());
        dto.setOpenDepartureId(saidaAberta.map(DepartureLog::getDepartureLogId).orElse(null));

        // Seção 3 — Uso por período
        List<String> periodos = List.of("hoje", "7", "30", "year");
        Map<String, Long>       saidasMap = new LinkedHashMap<>();
        Map<String, BigDecimal> kmMap     = new LinkedHashMap<>();
        for (String p : periodos) {
            LocalDateTime ini = dataInicio(p);
            saidasMap.put(p, saidaRepo.countPorMatriculaEPeriodo(registration, ini));
            kmMap.put(p,     safe(saidaRepo.sumKmPorMatriculaEPeriodo(registration, ini)));
        }
        dto.setDeparturesByPeriod(saidasMap);
        dto.setKmByPeriod(kmMap);

        saidaRepo.findTopByUserRegistrationOrderByDateTimeDepartureDesc(registration).ifPresent(s -> {
            dto.setLastDepartureDate(s.getDateTimeDeparture() != null
                    ? s.getDateTimeDeparture().format(FMT_DATETIME) : null);
            dto.setLastDepartureVehicle(s.getVehicle() != null
                    ? s.getVehicle().getPrefix() + " — " + s.getVehicle().getLicensePlate() : null);
            dto.setLastDepartureDestination(s.getDestination());
        });

        // Seção 4 — Comportamento operacional
        dto.setAvgDepartureDurationHours(saidaRepo.calcularTempoMedioHoras(registration, start));
        dto.setLongestDepartureKm(safe(saidaRepo.buscarMaiorKm(registration, start)));
        dto.setLongestDepartureDurationHours(saidaRepo.buscarMaiorDuracaoHoras(registration, start));
        long saidasNoPeriodo = saidasMap.getOrDefault(periodo, 0L);
        dto.setDepartureFrequencyPerWeek(saidasNoPeriodo / numeroSemanas(periodo));

        // Seção 5 — Financeiro por período
        Map<String, BigDecimal> gastoMap  = new LinkedHashMap<>();
        Map<String, Long>       abastMap  = new LinkedHashMap<>();
        for (String p : periodos) {
            LocalDateTime ini = dataInicio(p);
            gastoMap.put(p, safe(abastRepo.sumSpendingByRegistrationAndPeriod(registration, ini)));
            abastMap.put(p, abastRepo.countFuelingsByRegistrationAndPeriod(registration, ini));
        }
        dto.setSpendingByPeriod(gastoMap);
        dto.setRefuelsByPeriod(abastMap);

        // Seção 6 — Manutenção
        dto.setOilChanges(trocaOleoRepo.countByDepartureLogUserRegistration(registration));
        trocaOleoRepo.findTopByDepartureLogUserRegistrationOrderByCreatedAtDesc(registration)
                .ifPresent(t -> dto.setLastOilChange(
                        t.getCreatedAt() != null ? t.getCreatedAt().format(FMT_DATETIME) : null));
        dto.setUsedVehicles(saidaRepo.buscarVeiculosUtilizados(registration));

        // Seção 7 — Documentos
        TechnicianReportDTO.DocumentosDTO documentos = new TechnicianReportDTO.DocumentosDTO();
        documentos.setReceived(docRepo.countByUserRegistration(registration));
        documentos.setRead(docRepo.countByUserRegistrationAndIsReadTrue(registration));
        documentos.setDownloaded(docRepo.countByUserRegistrationAndIsDownloadedTrue(registration));
        dto.setDocuments(documentos);

        // Destinos (top 5)
        List<FrequentDestinationDTO> destinos = new ArrayList<>();
        for (Object[] row : saidaRepo.buscarDestinosMaisFrequentes(registration)) {
            destinos.add(new FrequentDestinationDTO((String) row[0], ((Number) row[1]).longValue()));
        }
        dto.setDestinations(destinos);

        // Serviços
        Map<String, Long> servicos = new LinkedHashMap<>();
        for (Object[] row : saidaRepo.buscarServicosDoTecnico(registration, start)) {
            servicos.put(row[0] != null ? (String) row[0] : "Outros",
                    ((Number) row[1]).longValue());
        }
        dto.setServices(servicos);

        return dto;
    }
}
