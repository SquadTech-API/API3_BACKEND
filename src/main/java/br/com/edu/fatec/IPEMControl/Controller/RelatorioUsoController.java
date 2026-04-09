package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.RelatorioUsoMensalDTO;
import br.com.edu.fatec.IPEMControl.Service.RegistroSaidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/relatorios-uso")
@CrossOrigin(origins = "*")
public class RelatorioUsoController {

    @Autowired
    private RegistroSaidaService service;


    @GetMapping
    public ResponseEntity<RelatorioUsoMensalDTO> obterRelatorioUso(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        // Chama a lógica que soma os KMs e filtra as viagens
        RelatorioUsoMensalDTO relatorio = service.gerarRelatorioUsoMensal(inicio, fim);

        return ResponseEntity.ok(relatorio);
    }
}