package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.AdminDashboardSummaryDTO;
import br.com.edu.fatec.ipemControl.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    // GET /dashboard/summary — cards do topo do dashboard admin
    @GetMapping("/summary")
    public ResponseEntity<AdminDashboardSummaryDTO> summary() {
        return ResponseEntity.ok(adminDashboardService.getSummary());
    }
}