package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.BuscaAbastecimentoDTO;
import br.com.edu.fatec.IPEMControl.DTO.RelatorioAbastecimentoDTO;
import br.com.edu.fatec.IPEMControl.Service.AbastecimentoExportService;
import br.com.edu.fatec.IPEMControl.Service.AbastecimentoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/relatorios/abastecimento")
public class RelatorioAbastecimentoController {

    private final AbastecimentoService abastecimentoService;
    private final AbastecimentoExportService abastecimentoExportService;

    public RelatorioAbastecimentoController(
            AbastecimentoService abastecimentoService,
            AbastecimentoExportService abastecimentoExportService) {
        this.abastecimentoService = abastecimentoService;
        this.abastecimentoExportService = abastecimentoExportService;
    }

    // Retorna resumo geral do período informado
    @GetMapping("/geral")
    public ResponseEntity<RelatorioAbastecimentoDTO> geral(@RequestParam String periodo) {
        return ResponseEntity.ok(abastecimentoService.gerarRelatorio(periodo));
    }

    // Busca detalhada por data, intervalo ou veículo
    @GetMapping("/busca")
    public ResponseEntity<BuscaAbastecimentoDTO> busca(
            @RequestParam String tipo,
            @RequestParam(required = false) String data,
            @RequestParam(required = false) String de,
            @RequestParam(required = false) String ate,
            @RequestParam(required = false) String veiculo,
            @RequestParam(required = false, defaultValue = "ambos") String tipoRegistro) {
        return ResponseEntity.ok(
                abastecimentoService.buscar(tipo, data, de, ate, veiculo, tipoRegistro));
    }

    // Download do relatório nos formatos csv, excel, pdf ou docx
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(
            @RequestParam String formato,
            @RequestParam String periodo) {

        RelatorioAbastecimentoDTO relatorio = abastecimentoService.gerarRelatorio(periodo);
        byte[] arquivo = abastecimentoExportService.exportar(relatorio, formato);

        String nomeArquivo = "relatorio-abastecimento." + formato;
        MediaType tipoMidia = resolverTipoMidia(formato);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nomeArquivo)
                .contentType(tipoMidia)
                .body(arquivo);
    }

    private MediaType resolverTipoMidia(String formato) {
        return switch (formato) {
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