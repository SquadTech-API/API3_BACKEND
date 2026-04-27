package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.RelatorioVeiculoDTO;
import br.com.edu.fatec.IPEMControl.Service.RelatorioVeiculoExportService;
import br.com.edu.fatec.IPEMControl.Service.RelatorioVeiculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/relatorios/veiculo")
@CrossOrigin(origins = "*")
public class RelatorioVeiculoController {

    private final RelatorioVeiculoService relatorioVeiculoService;
    private final RelatorioVeiculoExportService relatorioVeiculoExportService;

    public RelatorioVeiculoController(
            RelatorioVeiculoService relatorioVeiculoService,
            RelatorioVeiculoExportService relatorioVeiculoExportService) {

        this.relatorioVeiculoService = relatorioVeiculoService;
        this.relatorioVeiculoExportService = relatorioVeiculoExportService;
    }

    @GetMapping("/{idVeiculo}")
    public ResponseEntity<RelatorioVeiculoDTO> gerarRelatorio(@PathVariable Integer idVeiculo) {
        return ResponseEntity.ok(relatorioVeiculoService.gerarRelatorioVeiculo(idVeiculo));
    }

    @GetMapping("/{idVeiculo}/pdf")
    public ResponseEntity<byte[]> exportarPdf(@PathVariable Integer idVeiculo) {
        return relatorioVeiculoExportService.exportarPdf(
                relatorioVeiculoService.gerarRelatorioVeiculo(idVeiculo), idVeiculo);
    }

    @GetMapping("/{idVeiculo}/csv")
    public ResponseEntity<byte[]> exportarCsv(@PathVariable Integer idVeiculo) {
        return relatorioVeiculoExportService.exportarCsv(
                relatorioVeiculoService.gerarRelatorioVeiculo(idVeiculo), idVeiculo);
    }

    @GetMapping("/{idVeiculo}/xlsx")
    public ResponseEntity<byte[]> exportarExcel(@PathVariable Integer idVeiculo) {
        return relatorioVeiculoExportService.exportarExcel(
                relatorioVeiculoService.gerarRelatorioVeiculo(idVeiculo), idVeiculo);
    }

    @GetMapping("/{idVeiculo}/docx")
    public ResponseEntity<byte[]> exportarDocx(@PathVariable Integer idVeiculo) {
        return relatorioVeiculoExportService.exportarDocx(
                relatorioVeiculoService.gerarRelatorioVeiculo(idVeiculo), idVeiculo);
    }
}