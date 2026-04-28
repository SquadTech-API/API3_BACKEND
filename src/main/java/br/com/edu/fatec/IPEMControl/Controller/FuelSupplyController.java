package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.FuelSupplyDTO;
import br.com.edu.fatec.IPEMControl.DTO.FuelSupplyHistoryDTO;
import br.com.edu.fatec.IPEMControl.DTO.FuelSupplySaveDTO;
import br.com.edu.fatec.IPEMControl.Service.FuelSupplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fuel-supply")
public class FuelSupplyController {

    private final FuelSupplyService fuelSupplyService;

    public FuelSupplyController(FuelSupplyService fuelSupplyService) {
        this.fuelSupplyService = fuelSupplyService;
    }

    // Registra um novo abastecimento
    @PostMapping
    public ResponseEntity<FuelSupplySaveDTO> create(@RequestBody FuelSupplyDTO dto) {
        return ResponseEntity.status(201).body(fuelSupplyService.save(dto));
    }

    // Retorna histórico de abastecimentos, com filtro opcional por veículo
    @GetMapping("/history")
    public ResponseEntity<List<FuelSupplyHistoryDTO>> history(
            @RequestParam(required = false) Integer vehicleId) {
        return ResponseEntity.ok(fuelSupplyService.findHistory(vehicleId));
    }
}