package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.ScheduleDTO;
import br.com.edu.fatec.ipemControl.dto.ScheduleResponseDTO;
import br.com.edu.fatec.ipemControl.security.UserPrincipal;
import br.com.edu.fatec.ipemControl.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // POST /schedules — técnico cria agendamento (#T05)
    @PostMapping
    public ResponseEntity<ScheduleResponseDTO> create(
            @RequestBody ScheduleDTO dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(201).body(
                scheduleService.create(dto, principal.getUser().getRegistration()));
    }

    // GET /schedules/my — agendamentos do técnico logado (#T05)
    @GetMapping("/my")
    public ResponseEntity<List<ScheduleResponseDTO>> findMy(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                scheduleService.findByRequester(principal.getUser().getRegistration()));
    }

    // GET /schedules — todos (admin)
    @GetMapping
    public ResponseEntity<List<ScheduleResponseDTO>> findAll(
            @RequestParam(required = false) String status) {
        if (status != null)
            return ResponseEntity.ok(scheduleService.findByStatus(status));
        return ResponseEntity.ok(scheduleService.findAll());
    }

    // PATCH /schedules/{id}/approve — admin aprova (#A13)
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ScheduleResponseDTO> approve(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                scheduleService.approve(id, principal.getUser().getRegistration()));
    }

    // PATCH /schedules/{id}/reject — admin rejeita (#A13)
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ScheduleResponseDTO> reject(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                scheduleService.reject(id, body.get("reason"),
                        principal.getUser().getRegistration()));
    }
}