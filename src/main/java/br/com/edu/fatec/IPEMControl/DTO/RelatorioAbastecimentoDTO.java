package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class RelatorioAbastecimentoDTO {

    // Cards de resumo
    private BigDecimal totalGasto;
    private BigDecimal totalLitros;
    private Integer quantidadeAbastecimentos;
    private Integer quantidadeTrocasOleo;
    private Integer quantidadeManutencaoAtrasada;
    private BigDecimal mediaConsumoKmL;
    private BigDecimal mediaCustoPorKm;

    // Gráfico de barras — 4 semanas
    private List<BigDecimal> gastoSemanal;
    private List<BigDecimal> litrosSemanal;

    // Tabela de registros individuais
    private List<AbastecimentoItemDTO> abastecimentos;

    // Dados por veículo
    private List<ConsumoVeiculoDTO> veiculos;

    // Histórico de trocas de óleo
    private List<ItemTrocaOleoDTO> trocasOleo;

    // Rankings
    private List<RankingUsuarioDTO> rankingUsuarios;
    private List<RankingPostoDTO> rankingPostos;
    private List<DistribuicaoCombustivelDTO> distribuicaoCombustivel;
}