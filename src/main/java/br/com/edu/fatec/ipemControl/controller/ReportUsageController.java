package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.service.DepartureLogService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@CrossOrigin(origins = "*")
public class ReportUsageController {

    private final DepartureLogService departureLogService;

    public ReportUsageController(DepartureLogService departureLogService) {
        this.departureLogService = departureLogService;
    }

    @GetMapping("/vehicle")
    public ResponseEntity<byte[]> downloadReport(
            @RequestParam Long vehicleId,
            @RequestParam String format,
            @RequestParam String period) {

        byte[] file = departureLogService.generateReportFile(vehicleId, format, period);

        MediaType mediaType = switch (format.toLowerCase()) {
            case "pdf"   -> MediaType.APPLICATION_PDF;
            case "csv"   -> MediaType.parseMediaType("text/csv; charset=UTF-8");
            case "excel" -> MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "docx"  -> MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            default      -> MediaType.APPLICATION_OCTET_STREAM;
        };

        String extension = switch (format.toLowerCase()) {
            case "pdf"   -> "pdf";
            case "csv"   -> "csv";
            case "excel" -> "xlsx";
            case "docx"  -> "docx";
            default      -> "bin";
        };

        String fileName = "vehicle_report_" + vehicleId + "." + extension;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(mediaType)
                .body(file);
    }
}
