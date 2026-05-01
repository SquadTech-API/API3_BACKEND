package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.RelatorioVeiculoDTO;
import br.com.edu.fatec.IPEMControl.Service.RelatorioVeiculoExportService;
import br.com.edu.fatec.IPEMControl.Service.RelatorioVeiculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/relatorios/veiculos")
@CrossOrigin("*")
public class RelatorioVeiculoController {

    private final RelatorioVeiculoService service;
    private final RelatorioVeiculoExportService exportService;

    public RelatorioVeiculoController(RelatorioVeiculoService service,
                                      RelatorioVeiculoExportService exportService) {
        this.service = service;
        this.exportService = exportService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelatorioVeiculoDTO> gerar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.gerarRelatorioVeiculo(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Integer id) {
        return exportService.exportarPdf(service.gerarRelatorioVeiculo(id), id);
    }

    @GetMapping("/{id}/csv")
    public ResponseEntity<byte[]> csv(@PathVariable Integer id) {
        return exportService.exportarCsv(service.gerarRelatorioVeiculo(id), id);
    }

    @GetMapping("/{id}/xlsx")
    public ResponseEntity<byte[]> excel(@PathVariable Integer id) {
        return exportService.exportarExcel(service.gerarRelatorioVeiculo(id), id);
    }

    @GetMapping("/{id}/docx")
    public ResponseEntity<byte[]> docx(@PathVariable Integer id) {
        return exportService.exportarDocx(service.gerarRelatorioVeiculo(id), id);
    }
}