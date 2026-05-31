package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.FuelReportDTO;
import br.com.edu.fatec.ipemControl.dto.FuelingSearchDTO;
import br.com.edu.fatec.ipemControl.service.FuelingExportService;
import br.com.edu.fatec.ipemControl.service.FuelingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report/fueling")
@RequiredArgsConstructor
public class ReportFuelingController {

    private final FuelingService fuelingService;
    private final FuelingExportService fuelingExportService;

    // GET /report/fueling/summary?period={}
    @GetMapping("/summary")
    public ResponseEntity<FuelReportDTO> summary(@RequestParam String period) {
        return ResponseEntity.ok(fuelingService.generateReport(period));
    }

    // GET /report/fueling/search
    @GetMapping("/search")
    public ResponseEntity<FuelingSearchDTO> search(
            @RequestParam String type,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String vehicle,
            @RequestParam(required = false, defaultValue = "both") String recordType) {
        return ResponseEntity.ok(
                fuelingService.search(type, date, from, to, vehicle, recordType));
    }

    // GET /report/fueling/download?format={}&period={}
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(
            @RequestParam String format,
            @RequestParam String period) {

        FuelReportDTO report = fuelingService.generateReport(period);
        byte[] file = fuelingExportService.export(report, format);

        MediaType mediaType = switch (format) {
            case "pdf"   -> MediaType.APPLICATION_PDF;
            case "csv"   -> MediaType.parseMediaType("text/csv; charset=UTF-8");
            case "excel" -> MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "docx"  -> MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            default      -> MediaType.APPLICATION_OCTET_STREAM;
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=fueling-report." + format)
                .contentType(mediaType)
                .body(file);
    }
}