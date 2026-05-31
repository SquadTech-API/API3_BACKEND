package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.*;
import br.com.edu.fatec.ipemControl.service.DepartureLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departure-logs")
@RequiredArgsConstructor
public class DepartureLogController {

    private final DepartureLogService departureLogService;

    // POST /departure-logs — abre saída
    @PostMapping
    public ResponseEntity<DepartureLogResponseDTO> openDeparture(
            @RequestBody DepartureLogDTO dto) {
        return ResponseEntity.status(201).body(departureLogService.openDeparture(dto));
    }

    // PATCH /departure-logs/{id}/return — registra retorno
    @PatchMapping("/{id}/return")
    public ResponseEntity<DepartureLogResponseDTO> registerReturn(
            @PathVariable Integer id,
            @RequestBody ReturnDTO dto) {
        return ResponseEntity.ok(departureLogService.registerReturn(id, dto));
    }

    // PATCH /departure-logs/{id}/mark-transcribed — SGI (#A09)
    @PatchMapping("/{id}/mark-transcribed")
    public ResponseEntity<DepartureLogResponseDTO> markTranscribed(
            @PathVariable Integer id) {
        return ResponseEntity.ok(departureLogService.markAsSgiTranscribed(id));
    }

    // GET /departure-logs
    @GetMapping
    public ResponseEntity<List<DepartureLogResponseDTO>> list() {
        return ResponseEntity.ok(departureLogService.findAll());
    }

    // GET /departure-logs/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DepartureLogResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(departureLogService.findById(id));
    }

    // GET /departure-logs/active?vehicleId={id}
    @GetMapping("/active")
    public ResponseEntity<DepartureLogResponseDTO> findActiveByVehicle(
            @RequestParam Integer vehicleId) {
        return ResponseEntity.ok(departureLogService.findActiveByVehicle(vehicleId));
    }

    // GET /departure-logs/active-user?registration={registration}
    @GetMapping("/active-user")
    public ResponseEntity<DepartureLogResponseDTO> findActiveByUser(
            @RequestParam Integer registration) {
        return ResponseEntity.ok(departureLogService.findActiveByUser(registration));
    }

    // GET /departure-logs/not-transcribed — saídas pendentes de SGI
    @GetMapping("/not-transcribed")
    public ResponseEntity<List<DepartureLogResponseDTO>> findNotTranscribed() {
        return ResponseEntity.ok(departureLogService.findNotTranscribed());
    }

    // GET /departure-logs/vehicle/{vehicleId}/oil-change
    @GetMapping("/vehicle/{vehicleId}/oil-change")
    public ResponseEntity<List<DepartureLogResponseDTO>> findOilChangeDepartures(
            @PathVariable Integer vehicleId) {
        return ResponseEntity.ok(
                departureLogService.findOilChangeDeparturesByVehicle(vehicleId));
    }
}