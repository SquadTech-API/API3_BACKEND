package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.MonthlyUsageReportDTO;
import br.com.edu.fatec.ipemControl.service.DepartureLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/report/usage")
@RequiredArgsConstructor
public class ReportUsageController {

    private final DepartureLogService departureLogService;

    // GET /report/usage/vehicle/{vehicleId}?period={}
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<MonthlyUsageReportDTO> vehicleReport(
            @PathVariable Integer vehicleId,
            @RequestParam(defaultValue = "30") String period) {

        LocalDateTime end   = LocalDateTime.now();
        LocalDateTime start = switch (period.toLowerCase()) {
            case "hoje" -> end.toLocalDate().atStartOfDay();
            case "7d"   -> end.minusDays(7);
            case "30d"  -> end.minusDays(30);
            case "1y"   -> end.minusYears(1);
            default     -> end.minusDays(30);
        };

        return ResponseEntity.ok(
                departureLogService.generateMonthlyReport(vehicleId, start, end));
    }
}