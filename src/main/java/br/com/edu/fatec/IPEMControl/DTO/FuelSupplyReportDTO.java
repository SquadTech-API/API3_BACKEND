package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class FuelSupplyReportDTO {

    //Cards de resumo
    private BigDecimal totalSpent;
    private BigDecimal totalLiters;
    private Integer fuelSupplyCount;
    private Integer oilChangeCount;
    private Integer overdueMaintenanceCount;
    private BigDecimal avgConsumptionKmL;
    private BigDecimal avgCostPerKm;

    //Gráfico de barras — 4 semanas
    private List<BigDecimal> weeklySpent;
    private List<BigDecimal> weeklyLiters;

    //Tabela de registros individuais
    private List<FuelSupplyItemDTO> fuelSupplies;

    //Dados por veículo
    private List<VehicleConsumptionDTO> vehicles;

    //Histórico de trocas de óleo
    private List<OilChangeItemDTO> oilChanges;

    //Rankings
    private List<UserRankingDTO> topUsers;
    private List<StationRankingDTO> topStations;
    private List<FuelTypeDistributionDTO> fuelDistribution;
}