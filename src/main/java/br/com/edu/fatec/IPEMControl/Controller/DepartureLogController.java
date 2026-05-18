package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.CloseExitDTO;
import br.com.edu.fatec.IPEMControl.DTO.DepartureLogDTO;
import br.com.edu.fatec.IPEMControl.DTO.DepartureLogResponseDTO;
import br.com.edu.fatec.IPEMControl.DTO.ReturnDTO;
import br.com.edu.fatec.IPEMControl.DTO.ReturnResponseDTO;
import br.com.edu.fatec.IPEMControl.Service.DepartureLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departure-logs")
@CrossOrigin(origins = "*")
public class DepartureLogController {

    private final DepartureLogService departureLogService;

    public DepartureLogController(DepartureLogService departureLogService) {
        this.departureLogService = departureLogService;
    }

    @PostMapping
    public ResponseEntity<DepartureLogResponseDTO> openDeparture(@RequestBody DepartureLogDTO dto) {
        return ResponseEntity.status(201).body(departureLogService.openDeparture(dto));
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<ReturnResponseDTO> registerReturn(
            @PathVariable Integer id,
            @RequestBody ReturnDTO dto) {
        return ResponseEntity.ok(departureLogService.registerReturn(id, dto));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<DepartureLogResponseDTO> closeDeparture(
            @PathVariable Integer id,
            @RequestBody CloseExitDTO dto) {
        return ResponseEntity.ok(departureLogService.closeDeparture(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<DepartureLogResponseDTO>> list() {
        return ResponseEntity.ok(departureLogService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartureLogResponseDTO> search(@PathVariable Integer id) {
        return ResponseEntity.ok(departureLogService.findById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<DepartureLogResponseDTO> findActiveByVehicle(@RequestParam Integer vehicleId) {
        return ResponseEntity.ok(departureLogService.findActiveByVehicle(vehicleId));
    }

    @GetMapping("/active-user")
    public ResponseEntity<DepartureLogResponseDTO> findActiveByUser(@RequestParam Integer registration) {
        return ResponseEntity.ok(departureLogService.findActiveByUser(registration));
    }

    @GetMapping("/vehicle/{vehicleId}/oil-change")
    public ResponseEntity<List<DepartureLogResponseDTO>> findOilChangeDepartures(@PathVariable Integer vehicleId) {
        return ResponseEntity.ok(departureLogService.findOilChangeDeparturesByVehicle(vehicleId));
    }
}
