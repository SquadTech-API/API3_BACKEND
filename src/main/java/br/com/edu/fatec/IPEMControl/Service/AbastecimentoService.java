package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.*;
import br.com.edu.fatec.IPEMControl.Entities.DepartureLog;
import br.com.edu.fatec.IPEMControl.Entities.Fueling;
import br.com.edu.fatec.IPEMControl.Entities.OilChange;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Repository.AbastecimentoRepository;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import br.com.edu.fatec.IPEMControl.Repository.TrocaOleoRepository;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AbastecimentoService {

    private final AbastecimentoRepository abastecimentoRepository;
    private final TrocaOleoRepository trocaOleoRepository;
    private final RegistroSaidaRepository registroSaidaRepository;
    private final VeiculoRepository veiculoRepository;

    public AbastecimentoService(
            AbastecimentoRepository abastecimentoRepository,
            TrocaOleoRepository trocaOleoRepository,
            RegistroSaidaRepository registroSaidaRepository,
            VeiculoRepository veiculoRepository) {
        this.abastecimentoRepository = abastecimentoRepository;
        this.trocaOleoRepository     = trocaOleoRepository;
        this.registroSaidaRepository  = registroSaidaRepository;
        this.veiculoRepository        = veiculoRepository;
    }

    // ── POST /abastecimento ──────────────────────────────────────────────────

    public SavedFuelingDTO salvar(FuelingDTO dto) {
        DepartureLog departureLog = registroSaidaRepository.findById(dto.getTripId())
                .orElseThrow(() -> new RuntimeException("Registro de saída não encontrado."));

        Fueling ab = new Fueling();
        ab.setDepartureLog(departureLog);
        ab.setDateTime(dto.getDateTime());
        ab.setFuelType(dto.getFuelType());
        ab.setLitersAmount(dto.getLitersQuantity());
        ab.setTotalValue(dto.getTotalAmount());
        ab.setFuelingKm(dto.getFuelingMileage());
        ab.setGasStationName(dto.getGasStationName());
        ab.setGasStationCity(dto.getGasStationCity());
        ab.setReceipt(dto.getInvoiceNumber());

        Fueling salvo = abastecimentoRepository.save(ab);

        return new SavedFuelingDTO(
                salvo.getFuelingId(),
                salvo.getDateTime(),
                salvo.getFuelType(),
                salvo.getLitersAmount(),
                salvo.getTotalValue(),
                salvo.getFuelingKm(),
                salvo.getGasStationName(),
                salvo.getGasStationCity(),
                salvo.getReceipt(),
                departureLog.getDepartureLogId()
        );
    }

    // ── GET /abastecimento/historico ─────────────────────────────────────────

    public List<FuelingHistoryDTO> buscarHistorico(Integer idVeiculo) {
        List<Fueling> lista = (idVeiculo != null)
                ? abastecimentoRepository.findByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(idVeiculo)
                : abastecimentoRepository.findAllByOrderByDataHoraDesc();
        return lista.stream().map(this::paraHistoricoDTO).collect(Collectors.toList());
    }

    private FuelingHistoryDTO paraHistoricoDTO(Fueling a) {
        DepartureLog rs    = a.getDepartureLog();
        Vehicle vehicle = rs != null ? rs.getVehicle() : null;
        String responsavel  = rs != null && rs.getUser() != null ? rs.getUser().getName() : null;

        return new FuelingHistoryDTO(
                a.getFuelingId(), a.getDateTime(), a.getFuelType(),
                a.getLitersAmount(), a.getTotalValue(), a.getFuelingKm(),
                a.getGasStationName(), a.getGasStationCity(), a.getReceipt(),
                vehicle != null ? vehicle.getVehicleId() : null,
                vehicle != null ? vehicle.getModel()    : null,
                vehicle != null ? vehicle.getPrefix()   : null,
                vehicle != null ? vehicle.getLicensePlate()     : null,
                responsavel
        );
    }

    // ── GET /relatorios/abastecimento/geral ──────────────────────────────────

    public FuelReportDTO gerarRelatorio(String periodo) {
        LocalDateTime dataInicio = resolverDataInicio(periodo);

        List<Fueling> fuelings = abastecimentoRepository
                .findByDataHoraAfterOrderByDataHoraDesc(dataInicio);

        BigDecimal totalGasto = fuelings.stream()
                .map(a -> a.getTotalValue() != null ? a.getTotalValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLitros = fuelings.stream()
                .map(a -> a.getLitersAmount() != null ? a.getLitersAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<OilChange> trocasOleo = trocaOleoRepository
                .findByCreatedAtAfterOrderByCreatedAtDesc(dataInicio);

        List<Vehicle> allVehicles = veiculoRepository.findAll();
        int quantidadeAtrasada = 0;
        for (Vehicle v : allVehicles) {
            Optional<OilChange> ultimaTroca = trocaOleoRepository.buscarUltimaPorVeiculo(v.getVehicleId());
            if (ultimaTroca.isPresent() && v.getCurrentKm() != null &&
                    v.getCurrentKm().compareTo(ultimaTroca.get().getNextChangeKm()) >= 0) {
                quantidadeAtrasada++;
            }
        }

        List<Object[]> linhasConsumo = abastecimentoRepository.buscarConsumoPorVeiculo(dataInicio);
        List<VehicleConsumptionDTO> consumoVeiculos = construirConsumoVeiculos(linhasConsumo, allVehicles);

        double mediaConsumo = consumoVeiculos.stream()
                .filter(v -> v.getFuelConsumptionKmPerLiter() != null).mapToDouble(VehicleConsumptionDTO::getFuelConsumptionKmPerLiter)
                .average().orElse(0);
        double mediaCusto = consumoVeiculos.stream()
                .filter(v -> v.getCostPerKilometer() != null).mapToDouble(VehicleConsumptionDTO::getCostPerKilometer)
                .average().orElse(0);

        List<Object[]> linhasSemanas = abastecimentoRepository.buscarEstatisticasSemanas(dataInicio);
        List<BigDecimal> gastoSemanal  = new ArrayList<>();
        List<BigDecimal> litrosSemanal = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i < linhasSemanas.size()) {
                gastoSemanal.add(paraBigDecimal(linhasSemanas.get(i)[1]));
                litrosSemanal.add(paraBigDecimal(linhasSemanas.get(i)[2]));
            } else {
                gastoSemanal.add(BigDecimal.ZERO);
                litrosSemanal.add(BigDecimal.ZERO);
            }
        }

        List<FuelingItemDTO> itensAbastecimento = fuelings.stream()
                .map(this::paraItemDTO).collect(Collectors.toList());

        // CORRIGIDO: usa getVehicle() direto da entidade OilChange
        List<OilChangeItemDTO> itensTrocaOleo = trocasOleo.stream()
                .map(this::paraItemTrocaOleoDTO).collect(Collectors.toList());

        List<UserRankingDTO>         rankingUsuarios  = construirRankingUsuarios(abastecimentoRepository.buscarRankingUsuarios(dataInicio));
        List<StationRankingDTO>           rankingPostos    = construirRankingPostos(abastecimentoRepository.buscarRankingPostos(dataInicio));
        List<FuelDistributionDTO> distribuicao    = construirDistribuicaoCombustivel(abastecimentoRepository.buscarDistribuicaoCombustivel(dataInicio));

        return new FuelReportDTO(
                totalGasto, totalLitros, fuelings.size(), trocasOleo.size(),
                quantidadeAtrasada,
                BigDecimal.valueOf(mediaConsumo).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(mediaCusto).setScale(2, RoundingMode.HALF_UP),
                gastoSemanal, litrosSemanal, itensAbastecimento,
                consumoVeiculos, itensTrocaOleo, rankingUsuarios, rankingPostos, distribuicao
        );
    }

    // ── GET /relatorios/abastecimento/busca ──────────────────────────────────

    public FuelingSearchDTO buscar(String tipo, String data, String de, String ate,
                                        String placa, String tipoRegistro) {
        List<Fueling> fuelings = new ArrayList<>();
        switch (tipo) {
            case "data" -> {
                LocalDateTime inicio = LocalDateTime.parse(data + "T00:00:00");
                LocalDateTime fim    = LocalDateTime.parse(data + "T23:59:59");
                fuelings = abastecimentoRepository.findByDataHoraBetweenOrderByDataHoraDesc(inicio, fim);
            }
            case "intervalo" -> {
                LocalDateTime inicio = LocalDateTime.parse(de + "T00:00:00");
                LocalDateTime fim    = LocalDateTime.parse(ate + "T23:59:59");
                fuelings = abastecimentoRepository.findByDataHoraBetweenOrderByDataHoraDesc(inicio, fim);
            }
            case "vehicle" -> fuelings =
                    abastecimentoRepository.findByRegistroSaidaVeiculoPlacaOrderByDataHoraDesc(placa);
        }

        List<FuelingItemDTO> itensAbastecimento = List.of();
        List<OilChangeItemDTO>     itensTrocaOleo     = List.of();

        if ("ambos".equals(tipoRegistro) || "abast".equals(tipoRegistro)) {
            itensAbastecimento = fuelings.stream().map(this::paraItemDTO).collect(Collectors.toList());
        }
        if ("ambos".equals(tipoRegistro) || "oleo".equals(tipoRegistro)) {
            itensTrocaOleo = trocaOleoRepository
                    .findByCreatedAtAfterOrderByCreatedAtDesc(resolverDataInicio("30"))
                    .stream().map(this::paraItemTrocaOleoDTO).collect(Collectors.toList());
        }

        List<VehicleConsumptionDTO> veiculos = construirConsumoVeiculos(
                abastecimentoRepository.buscarConsumoPorVeiculo(resolverDataInicio("30")),
                veiculoRepository.findAll());

        return new FuelingSearchDTO(itensAbastecimento, itensTrocaOleo, veiculos);
    }

    // ── Auxiliares ────────────────────────────────────────────────────────────

    private LocalDateTime resolverDataInicio(String periodo) {
        return switch (periodo) {
            case "hoje" -> LocalDateTime.now().toLocalDate().atStartOfDay();
            case "7"    -> LocalDateTime.now().minusDays(7);
            case "30"   -> LocalDateTime.now().minusDays(30);
            case "year"  -> LocalDateTime.now().minusYears(1);
            default     -> LocalDateTime.now().minusDays(30);
        };
    }

    private FuelingItemDTO paraItemDTO(Fueling a) {
        DepartureLog rs   = a.getDepartureLog();
        Vehicle vehicle = rs != null ? rs.getVehicle() : null;
        String responsavel = rs != null && rs.getUser() != null ? rs.getUser().getName() : null;
        return new FuelingItemDTO(
                a.getDateTime(),
                vehicle != null ? vehicle.getPrefix() : null,
                responsavel, a.getFuelType(), a.getLitersAmount(),
                a.getTotalValue(), a.getFuelingKm(), a.getGasStationName(),
                a.getGasStationCity(), a.getReceipt()
        );
    }

    /**
     * CORRIGIDO: agora resolve o Vehicle pelo campo direto t.getVehicle()
     * em vez de t.getDepartureLog().getVehicle() (que falha quando registroSaida é null
     * em trocas avulsas não vinculadas a uma saída).
     *
     * CORRIGIDO: ItemTrocaOleoDTO espera LocalDateTime — usa createdAt (timestamp do registro)
     * como aproximação aceitável enquanto oilChangeDate (LocalDate) não é adicionado ao DTO.
     */
    private OilChangeItemDTO  paraItemTrocaOleoDTO(OilChange t) {
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

    private List<VehicleConsumptionDTO> construirConsumoVeiculos(List<Object[]> linhas, List<Vehicle> todos) {
        return linhas.stream().map(linha -> {
            String placa          = (String) linha[0];
            BigDecimal litros     = paraBigDecimal(linha[1]);
            BigDecimal totalKm    = paraBigDecimal(linha[2]);
            double consumoKmL     = linha[3] != null ? ((Number) linha[3]).doubleValue() : 0;
            BigDecimal totalGasto = paraBigDecimal(linha[4]);

            double custoPorKm = (totalKm != null && totalKm.compareTo(BigDecimal.ZERO) > 0)
                    ? totalGasto.divide(totalKm, 4, RoundingMode.HALF_UP).doubleValue() : 0;

            Vehicle vehicle = todos.stream()
                    .filter(v -> v.getLicensePlate().equals(placa)).findFirst().orElse(null);

            Optional<OilChange> ultimaTroca = vehicle != null
                    ? trocaOleoRepository.buscarUltimaPorVeiculo(vehicle.getVehicleId())
                    : Optional.empty();

            return new VehicleConsumptionDTO(
                    placa,
                    vehicle != null ? vehicle.getCurrentKm() : null,
                    ultimaTroca.map(OilChange::getNextChangeKm).orElse(null),
                    ultimaTroca.map(OilChange::getChangeKm).orElse(null),
                    ultimaTroca.map(t -> t.getCreatedAt().toLocalDate().toString()).orElse(null),
                    consumoKmL, custoPorKm, totalGasto
            );
        }).collect(Collectors.toList());
    }

    private List<UserRankingDTO> construirRankingUsuarios(List<Object[]> linhas) {
        return linhas.stream().map(l -> new UserRankingDTO(
                (String) l[0], ((Number) l[1]).intValue(), paraBigDecimal(l[2])
        )).collect(Collectors.toList());
    }

    private List<StationRankingDTO> construirRankingPostos(List<Object[]> linhas) {
        return linhas.stream().map(l -> new StationRankingDTO(
                (String) l[0], (String) l[1], ((Number) l[2]).longValue()
        )).collect(Collectors.toList());
    }

    private List<FuelDistributionDTO> construirDistribuicaoCombustivel(List<Object[]> linhas) {
        long total = linhas.stream().mapToLong(l -> ((Number) l[1]).longValue()).sum();
        return linhas.stream().map(l -> {
            long q  = ((Number) l[1]).longValue();
            double pct = total > 0 ? Math.round((q * 100.0 / total) * 10.0) / 10.0 : 0.0;
            return new FuelDistributionDTO((String) l[0], pct);
        }).collect(Collectors.toList());
    }

    private BigDecimal paraBigDecimal(Object v) {
        return v != null ? new BigDecimal(v.toString()) : BigDecimal.ZERO;
    }
}