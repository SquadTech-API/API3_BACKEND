package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.GeneralReportDTO;
import br.com.edu.fatec.ipemControl.dto.TechnicianReportDTO;
import br.com.edu.fatec.ipemControl.service.ReportExportService;
import br.com.edu.fatec.ipemControl.service.TechnicianReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report/technical")
@RequiredArgsConstructor
public class ReportTechnicalController {

    private final TechnicianReportService technicianReportService;
    private final ReportExportService reportExportService;

    // GET /report/technical/summary?period={}
    @GetMapping("/summary")
    public ResponseEntity<GeneralReportDTO> summary(
            @RequestParam(defaultValue = "30") String period) {
        return ResponseEntity.ok(technicianReportService.generateSummary(period));
    }

    // GET /report/technical/{registration}?period={}
    @GetMapping("/{registration}")
    public ResponseEntity<TechnicianReportDTO> individual(
            @PathVariable Integer registration,
            @RequestParam(defaultValue = "30") String period) {
        return ResponseEntity.ok(
                technicianReportService.generateIndividualReport(registration, period));
    }

    // GET /report/technical/{registration}/download?format={}&period={}
    @GetMapping("/{registration}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Integer registration,
            @RequestParam(defaultValue = "pdf") String format,
            @RequestParam(defaultValue = "30") String period) {

        TechnicianReportDTO dto =
                technicianReportService.generateIndividualReport(registration, period);

        return switch (format) {
            case "csv"   -> reportExportService.exportCsvResponse(dto, registration);
            case "excel" -> reportExportService.exportExcelResponse(dto, registration);
            case "docx"  -> reportExportService.exportDocxResponse(dto, registration);
            default      -> reportExportService.exportPdfResponse(dto, registration);
        };
    }
}