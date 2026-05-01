package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.*;
import br.com.edu.fatec.IPEMControl.Entities.Abastecimento;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Entities.TrocaOleo;
import br.com.edu.fatec.IPEMControl.Entities.Veiculo;
import br.com.edu.fatec.IPEMControl.Repository.AbastecimentoRepository;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import br.com.edu.fatec.IPEMControl.Repository.TrocaOleoRepository;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        this.trocaOleoRepository = trocaOleoRepository;
        this.registroSaidaRepository = registroSaidaRepository;
        this.veiculoRepository = veiculoRepository;
    }

    // ── POST /abastecimento ──────────────────────────────────────────────────

    public AbastecimentoSalvoDTO salvar(AbastecimentoDTO dto) {
        RegistroSaida registroSaida = registroSaidaRepository.findById(dto.getIdSaida())
                .orElseThrow(() -> new RuntimeException("Registro de saída não encontrado."));

        Abastecimento abastecimento = new Abastecimento();
        abastecimento.setRegistroSaida(registroSaida);
        abastecimento.setDataHora(dto.getDataHora());
        abastecimento.setTipoCombustivel(dto.getTipoCombustivel());
        abastecimento.setQuantidadeLitros(dto.getQuantidadeLitros());
        abastecimento.setValorTotal(dto.getValorTotal());
        abastecimento.setKmAbastecimento(dto.getKmAbastecimento());
        abastecimento.setPostoNome(dto.getPostoNome());
        abastecimento.setPostoCidade(dto.getPostoCidade());
        abastecimento.setNotaFiscal(dto.getNotaFiscal());

        Abastecimento salvo = abastecimentoRepository.save(abastecimento);

        return new AbastecimentoSalvoDTO(
                salvo.getIdAbastecimento(),
                salvo.getDataHora(),
                salvo.getTipoCombustivel(),
                salvo.getQuantidadeLitros(),
                salvo.getValorTotal(),
                salvo.getKmAbastecimento(),
                salvo.getPostoNome(),
                salvo.getPostoCidade(),
                salvo.getNotaFiscal(),
                registroSaida.getIdSaida()
        );
    }

    // ── GET /abastecimento/historico ─────────────────────────────────────────

    public List<AbastecimentoHistoricoDTO> buscarHistorico(Integer idVeiculo) {
        List<Abastecimento> lista = (idVeiculo != null)
                ? abastecimentoRepository.findByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(idVeiculo)
                : abastecimentoRepository.findAllByOrderByDataHoraDesc();

        return lista.stream().map(this::paraHistoricoDTO).collect(Collectors.toList());
    }

    private AbastecimentoHistoricoDTO paraHistoricoDTO(Abastecimento a) {
        RegistroSaida registroSaida = a.getRegistroSaida();
        Veiculo veiculo = registroSaida != null ? registroSaida.getVeiculo() : null;
        String nomeResponsavel = registroSaida != null && registroSaida.getUsuario() != null
                ? registroSaida.getUsuario().getNome() : null;

        return new AbastecimentoHistoricoDTO(
                a.getIdAbastecimento(),
                a.getDataHora(),
                a.getTipoCombustivel(),
                a.getQuantidadeLitros(),
                a.getValorTotal(),
                a.getKmAbastecimento(),
                a.getPostoNome(),
                a.getPostoCidade(),
                a.getNotaFiscal(),
                veiculo != null ? veiculo.getIdVeiculo() : null,
                veiculo != null ? veiculo.getModelo()    : null,
                veiculo != null ? veiculo.getPrefixo()   : null,
                veiculo != null ? veiculo.getPlaca()     : null,
                nomeResponsavel
        );
    }

    // ── GET /relatorios/abastecimento/geral ──────────────────────────────────

    public RelatorioAbastecimentoDTO gerarRelatorio(String periodo) {
        LocalDateTime dataInicio = resolverDataInicio(periodo);

        List<Abastecimento> abastecimentos = abastecimentoRepository
                .findByDataHoraAfterOrderByDataHoraDesc(dataInicio);

        // Totais do período
        BigDecimal totalGasto = abastecimentos.stream()
                .map(a -> a.getValorTotal() != null ? a.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLitros = abastecimentos.stream()
                .map(a -> a.getQuantidadeLitros() != null ? a.getQuantidadeLitros() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Trocas de óleo no período
        List<TrocaOleo> trocasOleo = trocaOleoRepository
                .findByCreatedAtAfterOrderByCreatedAtDesc(dataInicio);

        // Veículos com manutenção atrasada
        List<Veiculo> todosVeiculos = veiculoRepository.findAll();
        int quantidadeAtrasada = 0;
        for (Veiculo v : todosVeiculos) {
            var ultimaTroca = trocaOleoRepository.buscarUltimaPorVeiculo(v.getIdVeiculo());
            if (ultimaTroca.isPresent() && v.getKmAtual() != null &&
                    v.getKmAtual().compareTo(ultimaTroca.get().getKmProximaTroca()) >= 0) {
                quantidadeAtrasada++;
            }
        }

        // Consumo e custo médio por veículo
        List<Object[]> linhasConsumo = abastecimentoRepository.buscarConsumoPorVeiculo(dataInicio);
        List<ConsumoVeiculoDTO> consumoVeiculos = construirConsumoVeiculos(linhasConsumo, todosVeiculos);

        double mediaConsumo = consumoVeiculos.stream()
                .filter(v -> v.getConsumoKmL() != null)
                .mapToDouble(ConsumoVeiculoDTO::getConsumoKmL)
                .average().orElse(0);

        double mediaCusto = consumoVeiculos.stream()
                .filter(v -> v.getCustoPorKm() != null)
                .mapToDouble(ConsumoVeiculoDTO::getCustoPorKm)
                .average().orElse(0);

        // Gráfico semanal — sempre 4 posições
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

        // Itens individuais para a tabela
        List<AbastecimentoItemDTO> itensAbastecimento = abastecimentos.stream()
                .map(this::paraItemDTO).collect(Collectors.toList());

        // Histórico de trocas de óleo
        List<ItemTrocaOleoDTO> itensTrocaOleo = trocasOleo.stream()
                .map(this::paraItemTrocaOleoDTO).collect(Collectors.toList());

        // Rankings
        List<RankingUsuarioDTO> rankingUsuarios = construirRankingUsuarios(
                abastecimentoRepository.buscarRankingUsuarios(dataInicio));
        List<RankingPostoDTO> rankingPostos = construirRankingPostos(
                abastecimentoRepository.buscarRankingPostos(dataInicio));
        List<DistribuicaoCombustivelDTO> distribuicao = construirDistribuicaoCombustivel(
                abastecimentoRepository.buscarDistribuicaoCombustivel(dataInicio));

        return new RelatorioAbastecimentoDTO(
                totalGasto,
                totalLitros,
                abastecimentos.size(),
                trocasOleo.size(),
                quantidadeAtrasada,
                BigDecimal.valueOf(mediaConsumo).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(mediaCusto).setScale(2, RoundingMode.HALF_UP),
                gastoSemanal,
                litrosSemanal,
                itensAbastecimento,
                consumoVeiculos,
                itensTrocaOleo,
                rankingUsuarios,
                rankingPostos,
                distribuicao
        );
    }

    // ── GET /relatorios/abastecimento/busca ──────────────────────────────────

    public BuscaAbastecimentoDTO buscar(String tipo, String data,
                                        String de, String ate,
                                        String placa, String tipoRegistro) {
        List<Abastecimento> abastecimentos = new ArrayList<>();

        // Filtra por tipo de busca
        switch (tipo) {
            case "data" -> {
                LocalDateTime inicio = LocalDateTime.parse(data + "T00:00:00");
                LocalDateTime fim    = LocalDateTime.parse(data + "T23:59:59");
                abastecimentos = abastecimentoRepository
                        .findByDataHoraBetweenOrderByDataHoraDesc(inicio, fim);
            }
            case "intervalo" -> {
                LocalDateTime inicio = LocalDateTime.parse(de + "T00:00:00");
                LocalDateTime fim    = LocalDateTime.parse(ate + "T23:59:59");
                abastecimentos = abastecimentoRepository
                        .findByDataHoraBetweenOrderByDataHoraDesc(inicio, fim);
            }
            case "veiculo" -> abastecimentos =
                    abastecimentoRepository.findByRegistroSaidaVeiculoPlacaOrderByDataHoraDesc(placa);
        }

        List<AbastecimentoItemDTO> itensAbastecimento = List.of();
        List<ItemTrocaOleoDTO> itensTrocaOleo = List.of();

        // Filtra por tipo de registro
        if ("ambos".equals(tipoRegistro) || "abast".equals(tipoRegistro)) {
            itensAbastecimento = abastecimentos.stream()
                    .map(this::paraItemDTO).collect(Collectors.toList());
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

    // ── Métodos auxiliares ───────────────────────────────────────────────────

    private LocalDateTime resolverDataInicio(String periodo) {
        return switch (periodo) {
            case "hoje" -> LocalDateTime.now().toLocalDate().atStartOfDay();
            case "7"    -> LocalDateTime.now().minusDays(7);
            case "30"   -> LocalDateTime.now().minusDays(30);
            case "ano"  -> LocalDateTime.now().minusYears(1);
            default     -> LocalDateTime.now().minusDays(30);
        };
    }

    private AbastecimentoItemDTO paraItemDTO(Abastecimento a) {
        RegistroSaida registroSaida = a.getRegistroSaida();
        Veiculo veiculo = registroSaida != null ? registroSaida.getVeiculo() : null;
        String responsavel = registroSaida != null && registroSaida.getUsuario() != null
                ? registroSaida.getUsuario().getNome() : null;

        return new AbastecimentoItemDTO(
                a.getDataHora(),
                veiculo != null ? veiculo.getPrefixo() : null,
                responsavel,
                a.getTipoCombustivel(),
                a.getQuantidadeLitros(),
                a.getValorTotal(),
                a.getKmAbastecimento(),
                a.getPostoNome(),
                a.getPostoCidade(),
                a.getNotaFiscal()
        );
    }

    private ItemTrocaOleoDTO paraItemTrocaOleoDTO(TrocaOleo t) {
        Veiculo veiculo = t.getRegistroSaida() != null
                ? t.getRegistroSaida().getVeiculo() : null;

        return new ItemTrocaOleoDTO(
                t.getCreatedAt(),
                veiculo != null ? veiculo.getPlaca() : null,
                t.getKmTroca(),
                t.getKmProximaTroca(),
                veiculo != null ? veiculo.getKmAtual() : null
        );
    }

    private List<ConsumoVeiculoDTO> construirConsumoVeiculos(
            List<Object[]> linhas, List<Veiculo> todosVeiculos) {
        return linhas.stream().map(linha -> {
            String placa          = (String) linha[0];
            BigDecimal litros     = paraBigDecimal(linha[1]);
            BigDecimal totalKm    = paraBigDecimal(linha[2]);
            double consumoKmL     = linha[3] != null ? ((Number) linha[3]).doubleValue() : 0;
            BigDecimal totalGasto = paraBigDecimal(linha[4]);

            double custoPorKm = (totalKm != null && totalKm.compareTo(BigDecimal.ZERO) > 0)
                    ? totalGasto.divide(totalKm, 4, RoundingMode.HALF_UP).doubleValue() : 0;

            Veiculo veiculo = todosVeiculos.stream()
                    .filter(v -> v.getPlaca().equals(placa))
                    .findFirst().orElse(null);

            Optional<TrocaOleo> ultimaTroca = veiculo != null
                    ? trocaOleoRepository.buscarUltimaPorVeiculo(veiculo.getIdVeiculo())
                    : Optional.empty();

            return new ConsumoVeiculoDTO(
                    placa,
                    veiculo != null ? veiculo.getKmAtual() : null,
                    ultimaTroca.map(TrocaOleo::getKmProximaTroca).orElse(null),
                    ultimaTroca.map(TrocaOleo::getKmTroca).orElse(null),
                    ultimaTroca.map(t -> t.getCreatedAt().toLocalDate().toString()).orElse(null),
                    consumoKmL,
                    custoPorKm,
                    totalGasto,
                    litros
            );
        }).collect(Collectors.toList());
    }

    private List<RankingUsuarioDTO> construirRankingUsuarios(List<Object[]> linhas) {
        return linhas.stream().map(linha -> new RankingUsuarioDTO(
                (String) linha[0],
                ((Number) linha[1]).intValue(),
                paraBigDecimal(linha[2])
        )).collect(Collectors.toList());
    }

    private List<RankingPostoDTO> construirRankingPostos(List<Object[]> linhas) {
        return linhas.stream().map(linha -> new RankingPostoDTO(
                (String) linha[0],
                (String) linha[1],
                ((Number) linha[2]).longValue()
        )).collect(Collectors.toList());
    }

    private List<DistribuicaoCombustivelDTO> construirDistribuicaoCombustivel(List<Object[]> linhas) {
        long total = linhas.stream()
                .mapToLong(linha -> ((Number) linha[1]).longValue())
                .sum();

        return linhas.stream().map(linha -> {
            long quantidade = ((Number) linha[1]).longValue();
            double percentual = total > 0
                    ? Math.round((quantidade * 100.0 / total) * 10.0) / 10.0
                    : 0.0;
            return new DistribuicaoCombustivelDTO(
                    (String) linha[0],
                    percentual
            );
        }).collect(Collectors.toList());
    }

    private BigDecimal paraBigDecimal(Object valor) {
        return valor != null ? new BigDecimal(valor.toString()) : BigDecimal.ZERO;
    }
}