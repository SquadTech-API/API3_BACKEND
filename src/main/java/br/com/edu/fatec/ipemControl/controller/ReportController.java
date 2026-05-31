package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.DailyReportDTO;
import br.com.edu.fatec.ipemControl.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // GET /report/daily?registration={}&date={}
    @GetMapping("/daily")
    public ResponseEntity<DailyReportDTO> dailyReport(
            @RequestParam Integer registration,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(
                reportService.generateDailyReportByTechnician(registration, date));
    }
}