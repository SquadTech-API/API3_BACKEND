package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.*;
import br.com.edu.fatec.ipemControl.service.DepartureLogService;
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

    // Endpoint adicionado para a atualização do status de transcrição do SGI (#A09.2)
    @PutMapping("/{id}/sgi-status")
    public ResponseEntity<DepartureLogResponseDTO> updateSgiStatus(
            @PathVariable Integer id,
            @RequestBody SGIStatusDTO dto) {
        return ResponseEntity.ok(departureLogService.updateSgiStatus(id, dto));
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