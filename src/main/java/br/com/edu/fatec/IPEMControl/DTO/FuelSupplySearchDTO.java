package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class FuelSupplySearchDTO {
    private List<FuelSupplyItemDTO> fuelSupplies;
    private List<OilChangeItemDTO> oilChanges;
    private List<VehicleConsumptionDTO> vehicles;
}
