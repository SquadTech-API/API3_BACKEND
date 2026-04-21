package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.*;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Entities.Usuario;
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
    @Autowired private TrocaOleoRepository        trocaOleoRepo;
    @Autowired private UsuarioDocumentoRepository docRepo;

    // ════════════════════════════════════════════════════════════════════════
    //  Utilitários
    // ════════════════════════════════════════════════════════════════════════

    private LocalDateTime dataInicio(String periodo) {
        return switch (periodo) {
            case "hoje" -> LocalDate.now().atStartOfDay();
            case "7"    -> LocalDateTime.now().minusDays(7);
            case "30"   -> LocalDateTime.now().minusDays(30);
            case "ano"  -> LocalDateTime.now().minusYears(1);
            default     -> LocalDate.now().atStartOfDay();
        };
    }

    private double numeroSemanas(String periodo) {
        return switch (periodo) {
            case "hoje" -> 1.0 / 7;
            case "7"    -> 1.0;
            case "30"   -> 4.0;
            case "ano"  -> 52.0;
            default     -> 1.0;
        };
    }

    private BigDecimal safe(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  GET /relatorios/tecnicos/geral
    // ════════════════════════════════════════════════════════════════════════

    public RelatorioGeralDTO gerarVisaoGeral(String periodo) {
        LocalDateTime inicio = dataInicio(periodo);

        // Saídas e KM por técnico (Query 3.1)
        List<Object[]> saidasKm = saidaRepo.buscarSaidasKmPorTecnico(inicio);

        // Custo por técnico (Query 3.2)
        List<Object[]> custos = abastRepo.buscarCustoPorTecnico(inicio);
        Map<Integer, BigDecimal> gastoMap = new HashMap<>();
        for (Object[] row : custos) {
            Integer mat    = ((Number) row[0]).intValue();
            BigDecimal val = row[1] instanceof BigDecimal bd ? bd
                    : BigDecimal.valueOf(((Number) row[1]).doubleValue());
            gastoMap.put(mat, val);
        }

        List<TecnicoResumoDTO>  tecnicos         = new ArrayList<>();
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

            tecnicos.add(new TecnicoResumoDTO(mat, nome, saidas, km));
            saidasPorTecnico.add(saidas);
            kmPorTecnico.add(km);
            gastoPorTecnico.add(gasto);

            totalSaidas += saidas;
            kmTotal      = kmTotal.add(km);
            custoTotal   = custoTotal.add(gasto);
        }

        // kmPorSemana — últimas 4 semanas, ordem cronológica
        List<Object[]>  semanasRaw  = saidaRepo.buscarKmPorSemana(inicio);
        List<BigDecimal> kmPorSemana = new ArrayList<>();
        for (Object[] row : semanasRaw) {
            BigDecimal val = row[1] != null
                    ? BigDecimal.valueOf(((Number) row[1]).doubleValue()).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            kmPorSemana.add(val);
        }
        while (kmPorSemana.size() < 4) kmPorSemana.add(BigDecimal.ZERO);
        Collections.reverse(kmPorSemana);

        RelatorioGeralDTO.ResumoGeral resumo = new RelatorioGeralDTO.ResumoGeral();
        resumo.setTotalSaidas(totalSaidas);
        resumo.setTecnicosAtivos(saidaRepo.countTecnicosAtivos());
        resumo.setKmTotal(kmTotal);
        resumo.setCustoTotal(custoTotal);
        resumo.setSaidasPorTecnico(saidasPorTecnico);
        resumo.setKmPorTecnico(kmPorTecnico);
        resumo.setGastoPorTecnico(gastoPorTecnico);
        resumo.setKmPorSemana(kmPorSemana);

        RelatorioGeralDTO dto = new RelatorioGeralDTO();
        dto.setResumo(resumo);
        dto.setTecnicos(tecnicos);
        return dto;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  GET /relatorios/tecnicos/{matricula}
    // ════════════════════════════════════════════════════════════════════════

    public RelatorioTecnicoDTO gerarRelatorioIndividual(Integer matricula, String periodo) {

        Usuario usuario = usuarioRepo.findByMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Técnico não encontrado."));

        LocalDateTime inicio = dataInicio(periodo);
        RelatorioTecnicoDTO dto = new RelatorioTecnicoDTO();

        // Seção 1 — Identificação
        dto.setMatricula(usuario.getMatricula());
        dto.setNome(usuario.getNome());
        dto.setCargo(usuario.getCargo());
        dto.setTipo(usuario.getTipoUsuario().name());
        dto.setCnh(usuario.getTipoHabilitacao() != null ? usuario.getTipoHabilitacao().name() : null);
        dto.setNumHabilitacao(usuario.getNumeroHabilitacao());
        dto.setCpf(usuario.getCpf());
        dto.setEmail(usuario.getEmail());
        dto.setTelefone(null); // campo não existe na entidade — adicionar futuramente
        dto.setDataNascimento(usuario.getDataNascimento() != null
                ? usuario.getDataNascimento().format(FMT_DATE) : null);

        // Seção 2 — Status operacional
        dto.setAtivo(usuario.getColaboradorAtivo());
        dto.setDataCadastro(usuario.getCreatedAt() != null
                ? usuario.getCreatedAt().format(FMT_DATETIME) : null);
        dto.setUltimaAtualizacao(usuario.getUpdatedAt() != null
                ? usuario.getUpdatedAt().format(FMT_DATETIME) : null);

        Optional<RegistroSaida> saidaAberta = saidaRepo
                .findTopByUsuarioMatriculaAndStatusOrderByDataHoraSaidaDesc(matricula, "em_andamento");
        dto.setSaidaEmAberto(saidaAberta.isPresent());
        dto.setIdSaidaAberta(saidaAberta.map(RegistroSaida::getIdSaida).orElse(null));

        // Seção 3 — Uso: mapas por todos os períodos
        List<String> periodos = List.of("hoje", "7", "30", "ano");
        Map<String, Long>       saidasMap = new LinkedHashMap<>();
        Map<String, BigDecimal> kmMap     = new LinkedHashMap<>();
        for (String p : periodos) {
            LocalDateTime ini = dataInicio(p);
            saidasMap.put(p, saidaRepo.countPorMatriculaEPeriodo(matricula, ini));
            kmMap.put(p,     saidaRepo.sumKmPorMatriculaEPeriodo(matricula, ini));
        }
        dto.setSaidasPorPeriodo(saidasMap);
        dto.setKmPorPeriodo(kmMap);

        saidaRepo.findTopByUsuarioMatriculaOrderByDataHoraSaidaDesc(matricula).ifPresent(s -> {
            dto.setUltimaSaidaData(s.getDataHoraSaida() != null
                    ? s.getDataHoraSaida().format(FMT_DATETIME) : null);
            dto.setUltimaSaidaVeiculo(s.getVeiculo() != null
                    ? s.getVeiculo().getPrefixo() + " — " + s.getVeiculo().getPlaca() : null);
            dto.setUltimaSaidaDestino(s.getLocalDestino());
        });

        // Seção 4 — Comportamento operacional
        dto.setTempoMedioSaidaHoras(saidaRepo.calcularTempoMedioHoras(matricula, inicio));
        dto.setMaiorSaidaKm(safe(saidaRepo.buscarMaiorKm(matricula, inicio)));
        dto.setMaiorSaidaDuracaoHoras(saidaRepo.buscarMaiorDuracaoHoras(matricula, inicio));
        long saidasNoPeriodo = saidasMap.getOrDefault(periodo, 0L);
        dto.setFrequenciaSaidasPorSemana(saidasNoPeriodo / numeroSemanas(periodo));

        // Seção 5 — Financeiro: mapas por todos os períodos
        Map<String, BigDecimal> gastoMap = new LinkedHashMap<>();
        Map<String, Long>       abastMap = new LinkedHashMap<>();
        for (String p : periodos) {
            LocalDateTime ini = dataInicio(p);
            gastoMap.put(p, safe(abastRepo.sumGastoPorMatriculaEPeriodo(matricula, ini)));
            abastMap.put(p, abastRepo.countAbastPorMatriculaEPeriodo(matricula, ini));
        }
        dto.setGastoPorPeriodo(gastoMap);
        dto.setAbastPorPeriodo(abastMap);

        // Seção 6 — Manutenção
        dto.setTrocasOleo(trocaOleoRepo.countByRegistroSaidaUsuarioMatricula(matricula));
        trocaOleoRepo.findTopByRegistroSaidaUsuarioMatriculaOrderByCreatedAtDesc(matricula)
                .ifPresent(t -> dto.setUltimaTrocaOleo(
                        t.getCreatedAt() != null ? t.getCreatedAt().format(FMT_DATETIME) : null));
        dto.setVeiculosUtilizados(saidaRepo.buscarVeiculosUtilizados(matricula));

        // Seção 7 — Documentos
        RelatorioTecnicoDTO.DocumentosDTO documentos = new RelatorioTecnicoDTO.DocumentosDTO();
        documentos.setRecebidos(docRepo.countByUsuarioMatricula(matricula));
        documentos.setLidos(docRepo.countByUsuarioMatriculaAndLidoTrue(matricula));
        documentos.setBaixados(docRepo.countByUsuarioMatriculaAndBaixadoTrue(matricula));
        dto.setDocumentos(documentos);

        // Destinos (top 5) e serviços
        List<DestinoFrequenteDTO> destinos = new ArrayList<>();
        for (Object[] row : saidaRepo.buscarDestinosMaisFrequentes(matricula)) {
            destinos.add(new DestinoFrequenteDTO((String) row[0], ((Number) row[1]).longValue()));
        }
        dto.setDestinos(destinos);

        Map<String, Long> servicos = new LinkedHashMap<>();
        for (Object[] row : saidaRepo.buscarServicosDoTecnico(matricula, inicio)) {
            servicos.put(row[0] != null ? (String) row[0] : "Outros",
                    ((Number) row[1]).longValue());
        }
        dto.setServicos(servicos);

        return dto;
    }
}