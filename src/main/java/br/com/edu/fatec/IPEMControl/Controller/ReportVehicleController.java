package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.VehicleReportDTO;
import br.com.edu.fatec.IPEMControl.Service.VehicleReportExportService;
import br.com.edu.fatec.IPEMControl.Service.VehicleReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report/vehicle")
@CrossOrigin("*")
public class ReportVehicleController {

    private final VehicleReportService service;
    private final VehicleReportExportService exportService;

    public ReportVehicleController(VehicleReportService service,
                                   VehicleReportExportService exportService) {
        this.service = service;
        this.exportService = exportService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleReportDTO> generate(@PathVariable Integer id) {
        return ResponseEntity.ok(service.generateVehicleReport(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Integer id) {
        return exportService.exportarPdf(service.generateVehicleReport(id), id);
    }

    @GetMapping("/{id}/csv")
    public ResponseEntity<byte[]> csv(@PathVariable Integer id) {
        return exportService.exportarCsv(service.generateVehicleReport(id), id);
    }

    @GetMapping("/{id}/xlsx")
    public ResponseEntity<byte[]> excel(@PathVariable Integer id) {
        return exportService.exportarExcel(service.generateVehicleReport(id), id);
    }

    @GetMapping("/{id}/docx")
    public ResponseEntity<byte[]> docx(@PathVariable Integer id) {
        return exportService.exportarDocx(service.generateVehicleReport(id), id);
    }
}
