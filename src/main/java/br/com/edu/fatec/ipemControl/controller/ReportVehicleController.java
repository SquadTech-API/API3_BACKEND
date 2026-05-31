package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.VehicleReportDTO;
import br.com.edu.fatec.ipemControl.service.VehicleReportExportService;
import br.com.edu.fatec.ipemControl.service.VehicleReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report/vehicle")
@RequiredArgsConstructor
public class ReportVehicleController {

    private final VehicleReportService vehicleReportService;
    private final VehicleReportExportService vehicleReportExportService;

    // GET /report/vehicle/{id}
    @GetMapping("/{id}")
    public ResponseEntity<VehicleReportDTO> generate(@PathVariable Integer id) {
        return ResponseEntity.ok(vehicleReportService.generateVehicleReport(id));
    }

    // GET /report/vehicle/{id}/pdf
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Integer id) {
        return vehicleReportExportService.exportPdf(
                vehicleReportService.generateVehicleReport(id), id);
    }

    // GET /report/vehicle/{id}/csv
    @GetMapping("/{id}/csv")
    public ResponseEntity<byte[]> csv(@PathVariable Integer id) {
        return vehicleReportExportService.exportCsv(
                vehicleReportService.generateVehicleReport(id), id);
    }

    // GET /report/vehicle/{id}/xlsx
    @GetMapping("/{id}/xlsx")
    public ResponseEntity<byte[]> excel(@PathVariable Integer id) {
        return vehicleReportExportService.exportExcel(
                vehicleReportService.generateVehicleReport(id), id);
    }
}