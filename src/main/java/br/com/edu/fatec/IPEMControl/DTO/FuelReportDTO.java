package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class FuelReportDTO {

    // Cards de summary
    private BigDecimal totalSpent;
    private BigDecimal totalLiters;
    private Integer refuelCount;
    private Integer oilChangeCount;
    private Integer overdueMaintenanceCount;
    private BigDecimal avgConsumptionKmL;
    private BigDecimal avgCostPerKm;

    // Gráfico de barras — 4 semanas
    private List<BigDecimal> weeklySpending;
    private List<BigDecimal> weeklyLiters;

    // Tabela de registros individuais
    private List<FuelingItemDTO> refuels;

    // Dados por veículo
    private List<VehicleConsumptionDTO> vehicles;

    // Histórico de trocas de óleo
    private List<ItemTrocaOleoDTO> oilChanges;

    // Rankings
    private List<UserRankingDTO> userRankings;
    private List<StationRankingDTO> stationRankings;
    private List<DistribuicaoCombustivelDTO> fuelDistribution;
}