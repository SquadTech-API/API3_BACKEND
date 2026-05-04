package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.Service.RegistroSaidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CORRIGIDO: switch de mediaType estava incompleto.
 *
 * Problemas anteriores:
 * 1. switch só tratava "pdf" e "csv" — "excel" e "docx" caíam em
 *    APPLICATION_OCTET_STREAM com extensão ".csv" (errado).
 * 2. RegistroSaidaService.gerarArquivoRelatorio() só gerava PDF real;
 *    "excel" e "docx" retornavam texto plano com extensão errada.
 *    Esse service foi atualizado separadamente para suportar os 4 formatos.
 *
 * Correções aplicadas:
 * - Cases adicionados para "excel" → .xlsx e "docx" → .docx
 * - Extensão do arquivo corrigida para cada formato
 * - Content-Disposition nome de arquivo correto
 */
@RestController
@RequestMapping("/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioUsoController {

    @Autowired
    private RegistroSaidaService service;

    @GetMapping("/viatura")
    public ResponseEntity<byte[]> downloadRelatorio(
            @RequestParam Long idVeiculo,
            @RequestParam String formato,
            @RequestParam String periodo) {

        byte[] arquivo = service.gerarArquivoRelatorio(idVeiculo, formato, periodo);

        // CORRIGIDO: switch completo com todos os 4 formatos que o frontend envia
        MediaType mediaType = switch (formato.toLowerCase()) {
            case "pdf"   -> MediaType.APPLICATION_PDF;
            case "csv"   -> MediaType.parseMediaType("text/csv; charset=UTF-8");
            case "excel" -> MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "docx"  -> MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            default      -> MediaType.APPLICATION_OCTET_STREAM;
        };

        // CORRIGIDO: extensão correta para cada formato
        String extensao = switch (formato.toLowerCase()) {
            case "pdf"   -> "pdf";
            case "csv"   -> "csv";
            case "excel" -> "xlsx";
            case "docx"  -> "docx";
            default      -> "bin";
        };

        String nomeArquivo = "relatorio_viatura_" + idVeiculo + "." + extensao;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nomeArquivo)
                .contentType(mediaType)
                .body(arquivo);
    }
}