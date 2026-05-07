package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.*;
import br.com.edu.fatec.IPEMControl.Entities.Fueling;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Entities.TrocaOleo;
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

    public AbastecimentoSalvoDTO salvar(AbastecimentoDTO dto) {
        RegistroSaida registroSaida = registroSaidaRepository.findById(dto.getIdSaida())
                .orElseThrow(() -> new RuntimeException("Registro de saída não encontrado."));

        Fueling ab = new Fueling();
        ab.setRegistroSaida(registroSaida);
        ab.setDateTime(dto.getDataHora());
        ab.setFuelType(dto.getTipoCombustivel());
        ab.setLitersAmount(dto.getQuantidadeLitros());
        ab.setTotalValue(dto.getValorTotal());
        ab.setFuelingKm(dto.getKmAbastecimento());
        ab.setGasStationName(dto.getPostoNome());
        ab.setGasStationCity(dto.getPostoCidade());
        ab.setReceipt(dto.getNotaFiscal());

        Fueling salvo = abastecimentoRepository.save(ab);

        return new AbastecimentoSalvoDTO(
                salvo.getFuelingId(),
                salvo.getDateTime(),
                salvo.getFuelType(),
                salvo.getLitersAmount(),
                salvo.getTotalValue(),
                salvo.getFuelingKm(),
                salvo.getGasStationName(),
                salvo.getGasStationCity(),
                salvo.getReceipt(),
                registroSaida.getIdSaida()
        );
    }

    // ── GET /abastecimento/historico ─────────────────────────────────────────

    public List<AbastecimentoHistoricoDTO> buscarHistorico(Integer idVeiculo) {
        List<Fueling> lista = (idVeiculo != null)
                ? abastecimentoRepository.findByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(idVeiculo)
                : abastecimentoRepository.findAllByOrderByDataHoraDesc();
        return lista.stream().map(this::paraHistoricoDTO).collect(Collectors.toList());
    }

    private AbastecimentoHistoricoDTO paraHistoricoDTO(Fueling a) {
        RegistroSaida rs    = a.getRegistroSaida();
        Vehicle vehicle = rs != null ? rs.getVehicle() : null;
        String responsavel  = rs != null && rs.getUsuario() != null ? rs.getUsuario().getNome() : null;

        return new AbastecimentoHistoricoDTO(
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

    public RelatorioAbastecimentoDTO gerarRelatorio(String periodo) {
        LocalDateTime dataInicio = resolverDataInicio(periodo);

        List<Fueling> fuelings = abastecimentoRepository
                .findByDataHoraAfterOrderByDataHoraDesc(dataInicio);

        BigDecimal totalGasto = fuelings.stream()
                .map(a -> a.getTotalValue() != null ? a.getTotalValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLitros = fuelings.stream()
                .map(a -> a.getLitersAmount() != null ? a.getLitersAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<TrocaOleo> trocasOleo = trocaOleoRepository
                .findByCreatedAtAfterOrderByCreatedAtDesc(dataInicio);

        List<Vehicle> allVehicles = veiculoRepository.findAll();
        int quantidadeAtrasada = 0;
        for (Vehicle v : allVehicles) {
            Optional<TrocaOleo> ultimaTroca = trocaOleoRepository.buscarUltimaPorVeiculo(v.getVehicleId());
            if (ultimaTroca.isPresent() && v.getCurrentKm() != null &&
                    v.getCurrentKm().compareTo(ultimaTroca.get().getKmProximaTroca()) >= 0) {
                quantidadeAtrasada++;
            }
        }

        List<Object[]> linhasConsumo = abastecimentoRepository.buscarConsumoPorVeiculo(dataInicio);
        List<ConsumoVeiculoDTO> consumoVeiculos = construirConsumoVeiculos(linhasConsumo, allVehicles);

        double mediaConsumo = consumoVeiculos.stream()
                .filter(v -> v.getConsumoKmL() != null).mapToDouble(ConsumoVeiculoDTO::getConsumoKmL)
                .average().orElse(0);
        double mediaCusto = consumoVeiculos.stream()
                .filter(v -> v.getCustoPorKm() != null).mapToDouble(ConsumoVeiculoDTO::getCustoPorKm)
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

        List<AbastecimentoItemDTO> itensAbastecimento = fuelings.stream()
                .map(this::paraItemDTO).collect(Collectors.toList());

        // CORRIGIDO: usa getVehicle() direto da entidade TrocaOleo
        List<ItemTrocaOleoDTO> itensTrocaOleo = trocasOleo.stream()
                .map(this::paraItemTrocaOleoDTO).collect(Collectors.toList());

        List<RankingUsuarioDTO>         rankingUsuarios  = construirRankingUsuarios(abastecimentoRepository.buscarRankingUsuarios(dataInicio));
        List<RankingPostoDTO>           rankingPostos    = construirRankingPostos(abastecimentoRepository.buscarRankingPostos(dataInicio));
        List<DistribuicaoCombustivelDTO> distribuicao    = construirDistribuicaoCombustivel(abastecimentoRepository.buscarDistribuicaoCombustivel(dataInicio));

        return new RelatorioAbastecimentoDTO(
                totalGasto, totalLitros, fuelings.size(), trocasOleo.size(),
                quantidadeAtrasada,
                BigDecimal.valueOf(mediaConsumo).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(mediaCusto).setScale(2, RoundingMode.HALF_UP),
                gastoSemanal, litrosSemanal, itensAbastecimento,
                consumoVeiculos, itensTrocaOleo, rankingUsuarios, rankingPostos, distribuicao
        );
    }

    // ── GET /relatorios/abastecimento/busca ──────────────────────────────────

    public BuscaAbastecimentoDTO buscar(String tipo, String data, String de, String ate,
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
            case "veiculo" -> fuelings =
                    abastecimentoRepository.findByRegistroSaidaVeiculoPlacaOrderByDataHoraDesc(placa);
        }

        List<AbastecimentoItemDTO> itensAbastecimento = List.of();
        List<ItemTrocaOleoDTO>     itensTrocaOleo     = List.of();

        if ("ambos".equals(tipoRegistro) || "abast".equals(tipoRegistro)) {
            itensAbastecimento = fuelings.stream().map(this::paraItemDTO).collect(Collectors.toList());
        }
        if ("ambos".equals(tipoRegistro) || "oleo".equals(tipoRegistro)) {
            itensTrocaOleo = trocaOleoRepository
                    .findByCreatedAtAfterOrderByCreatedAtDesc(resolverDataInicio("30"))
                    .stream().map(this::paraItemTrocaOleoDTO).collect(Collectors.toList());
        }

        List<ConsumoVeiculoDTO> veiculos = construirConsumoVeiculos(
                abastecimentoRepository.buscarConsumoPorVeiculo(resolverDataInicio("30")),
                veiculoRepository.findAll());

        return new BuscaAbastecimentoDTO(itensAbastecimento, itensTrocaOleo, veiculos);
    }

    // ── Auxiliares ────────────────────────────────────────────────────────────

    private LocalDateTime resolverDataInicio(String periodo) {
        return switch (periodo) {
            case "hoje" -> LocalDateTime.now().toLocalDate().atStartOfDay();
            case "7"    -> LocalDateTime.now().minusDays(7);
            case "30"   -> LocalDateTime.now().minusDays(30);
            case "ano"  -> LocalDateTime.now().minusYears(1);
            default     -> LocalDateTime.now().minusDays(30);
        };
    }

    private AbastecimentoItemDTO paraItemDTO(Fueling a) {
        RegistroSaida rs   = a.getRegistroSaida();
        Vehicle vehicle = rs != null ? rs.getVehicle() : null;
        String responsavel = rs != null && rs.getUsuario() != null ? rs.getUsuario().getNome() : null;
        return new AbastecimentoItemDTO(
                a.getDateTime(),
                vehicle != null ? vehicle.getPrefix() : null,
                responsavel, a.getFuelType(), a.getLitersAmount(),
                a.getTotalValue(), a.getFuelingKm(), a.getGasStationName(),
                a.getGasStationCity(), a.getReceipt()
        );
    }

    /**
     * CORRIGIDO: agora resolve o Vehicle pelo campo direto t.getVehicle()
     * em vez de t.getRegistroSaida().getVehicle() (que falha quando registroSaida é null
     * em trocas avulsas não vinculadas a uma saída).
     *
     * CORRIGIDO: ItemTrocaOleoDTO espera LocalDateTime — usa createdAt (timestamp do registro)
     * como aproximação aceitável enquanto dataTroca (LocalDate) não é adicionado ao DTO.
     */
    private ItemTrocaOleoDTO paraItemTrocaOleoDTO(TrocaOleo t) {
        // Usa o vínculo direto com Vehicle adicionado na entidade corrigida
        Vehicle vehicle = t.getVehicle();

        // Se por algum motivo o vínculo direto for null, tenta via registroSaida
        if (vehicle == null && t.getRegistroSaida() != null) {
            vehicle = t.getRegistroSaida().getVehicle();
        }

        return new ItemTrocaOleoDTO(
                t.getCreatedAt(),                                        // LocalDateTime — timestamp
                vehicle != null ? vehicle.getLicensePlate()     : null,
                t.getKmTroca(),
                t.getKmProximaTroca(),
                vehicle != null ? vehicle.getCurrentKm()   : null
        );
    }

    private List<ConsumoVeiculoDTO> construirConsumoVeiculos(List<Object[]> linhas, List<Vehicle> todos) {
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

            Optional<TrocaOleo> ultimaTroca = vehicle != null
                    ? trocaOleoRepository.buscarUltimaPorVeiculo(vehicle.getVehicleId())
                    : Optional.empty();

            return new ConsumoVeiculoDTO(
                    placa,
                    vehicle != null ? vehicle.getCurrentKm() : null,
                    ultimaTroca.map(TrocaOleo::getKmProximaTroca).orElse(null),
                    ultimaTroca.map(TrocaOleo::getKmTroca).orElse(null),
                    ultimaTroca.map(t -> t.getCreatedAt().toLocalDate().toString()).orElse(null),
                    consumoKmL, custoPorKm, totalGasto, litros
            );
        }).collect(Collectors.toList());
    }

    private List<RankingUsuarioDTO> construirRankingUsuarios(List<Object[]> linhas) {
        return linhas.stream().map(l -> new RankingUsuarioDTO(
                (String) l[0], ((Number) l[1]).intValue(), paraBigDecimal(l[2])
        )).collect(Collectors.toList());
    }

    private List<RankingPostoDTO> construirRankingPostos(List<Object[]> linhas) {
        return linhas.stream().map(l -> new RankingPostoDTO(
                (String) l[0], (String) l[1], ((Number) l[2]).longValue()
        )).collect(Collectors.toList());
    }

    private List<DistribuicaoCombustivelDTO> construirDistribuicaoCombustivel(List<Object[]> linhas) {
        long total = linhas.stream().mapToLong(l -> ((Number) l[1]).longValue()).sum();
        return linhas.stream().map(l -> {
            long q  = ((Number) l[1]).longValue();
            double pct = total > 0 ? Math.round((q * 100.0 / total) * 10.0) / 10.0 : 0.0;
            return new DistribuicaoCombustivelDTO((String) l[0], pct);
        }).collect(Collectors.toList());
    }

    private BigDecimal paraBigDecimal(Object v) {
        return v != null ? new BigDecimal(v.toString()) : BigDecimal.ZERO;
    }
}