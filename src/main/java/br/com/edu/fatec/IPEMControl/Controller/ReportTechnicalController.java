package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.GeneralReportDTO;
import br.com.edu.fatec.IPEMControl.DTO.TechnicianReportDTO;
import br.com.edu.fatec.IPEMControl.Service.ReportExportService;
import br.com.edu.fatec.IPEMControl.Service.TechnicianReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report/technical")
@CrossOrigin(origins = "*")
public class ReportTechnicalController {

    private final TechnicianReportService technicianReportService;
    private final ReportExportService exportService;

    public ReportTechnicalController(TechnicianReportService technicianReportService,
                                     ReportExportService exportService) {
        this.technicianReportService = technicianReportService;
        this.exportService = exportService;
    }

    @GetMapping("/summary")
    public ResponseEntity<GeneralReportDTO> summary(
            @RequestParam(defaultValue = "30") String period) {
        return ResponseEntity.ok(technicianReportService.generateSummary(period));
    }

    @GetMapping("/{registration}")
    public ResponseEntity<TechnicianReportDTO> individual(
            @PathVariable Integer registration,
            @RequestParam(defaultValue = "30") String period) {
        return ResponseEntity.ok(technicianReportService.generateIndividualReport(registration, period));
    }

    @GetMapping("/{registration}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Integer registration,
            @RequestParam(defaultValue = "pdf") String format,
            @RequestParam(defaultValue = "30") String period) {

        TechnicianReportDTO dto = technicianReportService.generateIndividualReport(registration, period);

        return switch (format) {
            case "csv"   -> exportService.exportarCsvResponse(dto, registration);
            case "excel" -> exportService.exportarExcelResponse(dto, registration);
            case "docx"  -> exportService.exportarDocxResponse(dto, registration);
            default      -> exportService.exportarPdfResponse(dto, registration);
        };
    }
}
