package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.FuelSupplyReportDTO;
import br.com.edu.fatec.IPEMControl.DTO.FuelSupplySearchDTO;
import br.com.edu.fatec.IPEMControl.Service.FuelSupplyExportService;
import br.com.edu.fatec.IPEMControl.Service.FuelSupplyService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports/fuel-supply")
public class FuelSupplyReportController {

    private final FuelSupplyService fuelSupplyService;
    private final FuelSupplyExportService fuelSupplyExportService;

    public FuelSupplyReportController(
            FuelSupplyService fuelSupplyService,
            FuelSupplyExportService fuelSupplyExportService) {
        this.fuelSupplyService = fuelSupplyService;
        this.fuelSupplyExportService = fuelSupplyExportService;
    }

    // Retorna resumo geral do período informado
    @GetMapping("/general")
    public ResponseEntity<FuelSupplyReportDTO> general(@RequestParam String period) {
        return ResponseEntity.ok(fuelSupplyService.generateReport(period));
    }

    // Busca detalhada por data, intervalo ou veículo
    @GetMapping("/search")
    public ResponseEntity<FuelSupplySearchDTO> search(
            @RequestParam String type,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String plate,
            @RequestParam(required = false, defaultValue = "ambos") String recordType) {
        return ResponseEntity.ok(
                fuelSupplyService.search(type, date, from, to, plate, recordType));
    }

    // Download do relatório nos formatos csv, excel, pdf ou docx
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(
            @RequestParam String format,
            @RequestParam String period) {

        FuelSupplyReportDTO report = fuelSupplyService.generateReport(period);
        byte[] file = fuelSupplyExportService.export(report, format);

        String filename = "fuel-supply-report." + format;
        MediaType mediaType = resolveMediaType(format);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(file);
    }

    private MediaType resolveMediaType(String format) {
        return switch (format) {
            case "pdf"   -> MediaType.APPLICATION_PDF;
            case "csv"   -> MediaType.parseMediaType("text/csv; charset=UTF-8");
            case "excel" -> MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "docx"  -> MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            default      -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}