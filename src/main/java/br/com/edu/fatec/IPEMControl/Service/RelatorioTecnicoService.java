package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.DestinoFrequenteDTO;
import br.com.edu.fatec.IPEMControl.DTO.GeneralReportDTO;
import br.com.edu.fatec.IPEMControl.DTO.TechnicianReportDTO;
import br.com.edu.fatec.IPEMControl.DTO.TechnicianSummaryDTO;
import br.com.edu.fatec.IPEMControl.Entities.DepartureLog;
import br.com.edu.fatec.IPEMControl.Entities.User;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RelatorioTecnicoService {

    private static final DateTimeFormatter FMT_DATE     = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired private RegistroSaidaRepository   saidaRepo;
    @Autowired private AbastecimentoRepository    abastRepo;
    @Autowired private UsuarioRepository          usuarioRepo;
    @Autowired private OilChangeRepository trocaOleoRepo;
    @Autowired private UsuarioDocumentoRepository docRepo;

    // ════════════════════════════════════════════════════════════════════════
    //  Utilitários
    // ════════════════════════════════════════════════════════════════════════

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

    public GeneralReportDTO gerarVisaoGeral(String periodo) {
        LocalDateTime inicio = dataInicio(periodo);

        List<Object[]> saidasKm = saidaRepo.buscarSaidasKmPorTecnico(inicio);

        List<Object[]> custos = abastRepo.buscarCustoPorTecnico(inicio);
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

        List<Object[]>   semanasRaw  = saidaRepo.buscarKmPorSemana(inicio);
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

    public TechnicianReportDTO gerarRelatorioIndividual(Integer matricula, String periodo) {

        User user = usuarioRepo.findByMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Técnico não encontrado."));

        LocalDateTime inicio = dataInicio(periodo);
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
                .findTopByUsuarioMatriculaAndStatusOrderByDataHoraSaidaDesc(matricula, "em_andamento");
        dto.setOpenDeparture(saidaAberta.isPresent());
        dto.setOpenDepartureId(saidaAberta.map(DepartureLog::getDepartureLogId).orElse(null));

        // Seção 3 — Uso por período
        List<String> periodos = List.of("hoje", "7", "30", "year");
        Map<String, Long>       saidasMap = new LinkedHashMap<>();
        Map<String, BigDecimal> kmMap     = new LinkedHashMap<>();
        for (String p : periodos) {
            LocalDateTime ini = dataInicio(p);
            saidasMap.put(p, saidaRepo.countPorMatriculaEPeriodo(matricula, ini));
            kmMap.put(p,     safe(saidaRepo.sumKmPorMatriculaEPeriodo(matricula, ini)));
        }
        dto.setDeparturesByPeriod(saidasMap);
        dto.setKmByPeriod(kmMap);

        saidaRepo.findTopByUsuarioMatriculaOrderByDataHoraSaidaDesc(matricula).ifPresent(s -> {
            dto.setLastDepartureDate(s.getDateTimeDeparture() != null
                    ? s.getDateTimeDeparture().format(FMT_DATETIME) : null);
            dto.setLastDepartureVehicle(s.getVehicle() != null
                    ? s.getVehicle().getPrefix() + " — " + s.getVehicle().getLicensePlate() : null);
            dto.setLastDepartureDestination(s.getDestination());
        });

        // Seção 4 — Comportamento operacional
        dto.setAvgDepartureDurationHours(saidaRepo.calcularTempoMedioHoras(matricula, inicio));
        dto.setLongestDepartureKm(safe(saidaRepo.buscarMaiorKm(matricula, inicio)));
        dto.setLongestDepartureDurationHours(saidaRepo.buscarMaiorDuracaoHoras(matricula, inicio));
        long saidasNoPeriodo = saidasMap.getOrDefault(periodo, 0L);
        dto.setDepartureFrequencyPerWeek(saidasNoPeriodo / numeroSemanas(periodo));

        // Seção 5 — Financeiro por período
        Map<String, BigDecimal> gastoMap  = new LinkedHashMap<>();
        Map<String, Long>       abastMap  = new LinkedHashMap<>();
        for (String p : periodos) {
            LocalDateTime ini = dataInicio(p);
            gastoMap.put(p, safe(abastRepo.sumGastoPorMatriculaEPeriodo(matricula, ini)));
            abastMap.put(p, abastRepo.countAbastPorMatriculaEPeriodo(matricula, ini));
        }
        dto.setSpendingByPeriod(gastoMap);
        dto.setRefuelsByPeriod(abastMap);

        // Seção 6 — Manutenção
        dto.setOilChanges(trocaOleoRepo.countByRegistroSaidaUsuarioMatricula(matricula));
        trocaOleoRepo.findTopByRegistroSaidaUsuarioMatriculaOrderByCreatedAtDesc(matricula)
                .ifPresent(t -> dto.setLastOilChange(
                        t.getCreatedAt() != null ? t.getCreatedAt().format(FMT_DATETIME) : null));
        dto.setUsedVehicles(saidaRepo.buscarVeiculosUtilizados(matricula));

        // Seção 7 — Documentos
        TechnicianReportDTO.DocumentosDTO documentos = new TechnicianReportDTO.DocumentosDTO();
        documentos.setReceived(docRepo.countByUsuarioMatricula(matricula));
        documentos.setRead(docRepo.countByUsuarioMatriculaAndLidoTrue(matricula));
        documentos.setDownloaded(docRepo.countByUsuarioMatriculaAndBaixadoTrue(matricula));
        dto.setDocuments(documentos);

        // Destinos (top 5)
        List<DestinoFrequenteDTO> destinos = new ArrayList<>();
        for (Object[] row : saidaRepo.buscarDestinosMaisFrequentes(matricula)) {
            destinos.add(new DestinoFrequenteDTO((String) row[0], ((Number) row[1]).longValue()));
        }
        dto.setDestinations(destinos);

        // Serviços
        Map<String, Long> servicos = new LinkedHashMap<>();
        for (Object[] row : saidaRepo.buscarServicosDoTecnico(matricula, inicio)) {
            servicos.put(row[0] != null ? (String) row[0] : "Outros",
                    ((Number) row[1]).longValue());
        }
        dto.setServices(servicos);

        return dto;
    }
}