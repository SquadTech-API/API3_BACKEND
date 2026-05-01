package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.Service.RegistroSaidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioUsoController {

    @Autowired
    private RegistroSaidaService service;

    /**
     * Endpoint principal para download de relatórios.
     * Certifique-se de que o front-end envie os parâmetros idVeiculo, formato e periodo.
     */
    @GetMapping("/viatura")
    public ResponseEntity<byte[]> downloadRelatorio(
            @RequestParam Long idVeiculo,
            @RequestParam String formato,
            @RequestParam String periodo) {

        // O Service já está ajustado para filtrar por DataHoraSaida conforme nossa última correção
        byte[] arquivo = service.gerarArquivoRelatorio(idVeiculo, formato, periodo);

        // Define o tipo de arquivo de retorno com base no formato solicitado
        MediaType mediaType = switch (formato.toLowerCase()) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "csv" -> MediaType.parseMediaType("text/csv");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };

        // Define a extensão do arquivo no download
        String extensao = formato.toLowerCase().equals("pdf") ? "pdf" : "csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_veiculo_" + idVeiculo + "." + extensao)
                .contentType(mediaType)
                .body(arquivo);
    }
}