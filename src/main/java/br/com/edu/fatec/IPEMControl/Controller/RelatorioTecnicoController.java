package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.GeneralReportDTO;
import br.com.edu.fatec.IPEMControl.DTO.TechnicianReportDTO;
import br.com.edu.fatec.IPEMControl.Service.RelatorioExportService;
import br.com.edu.fatec.IPEMControl.Service.RelatorioTecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/relatorios/tecnicos")
@CrossOrigin(origins = "*")
public class RelatorioTecnicoController {

    @Autowired
    private RelatorioTecnicoService relatorioTecnicoService;

    @Autowired
    private RelatorioExportService exportService;

    /**
     * GET /relatorios/technicians/geral?periodo={periodo}
     * periodo: hoje | 7 | 30 | year  (padrão: 30)
     */
    @GetMapping("/geral")
    public ResponseEntity<GeneralReportDTO> visaoGeral(
            @RequestParam(defaultValue = "30") String periodo) {
        return ResponseEntity.ok(relatorioTecnicoService.gerarVisaoGeral(periodo));
    }

    /**
     * GET /relatorios/technicians/{registration}?periodo={periodo}
     * periodo: hoje | 7 | 30 | year  (padrão: 30)
     */
    @GetMapping("/{matricula}")
    public ResponseEntity<TechnicianReportDTO> individual(
            @PathVariable Integer matricula,
            @RequestParam(defaultValue = "30") String periodo) {
        return ResponseEntity.ok(relatorioTecnicoService.gerarRelatorioIndividual(matricula, periodo));
    }

    /**
     * GET /relatorios/technicians/{registration}/download?formato={fmt}&periodo={periodo}
     * formato: pdf | csv | excel | docx  (padrão: pdf)
     * periodo: hoje | 7 | 30 | year        (padrão: 30)
     */
    @GetMapping("/{matricula}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Integer matricula,
            @RequestParam(defaultValue = "pdf") String formato,
            @RequestParam(defaultValue = "30")  String periodo) {

        TechnicianReportDTO dto = relatorioTecnicoService.gerarRelatorioIndividual(matricula, periodo);

        return switch (formato) {
            case "csv"   -> exportService.exportarCsvResponse(dto, matricula);
            case "excel" -> exportService.exportarExcelResponse(dto, matricula);
            case "docx"  -> exportService.exportarDocxResponse(dto, matricula);
            default      -> exportService.exportarPdfResponse(dto, matricula);
        };
    }
}