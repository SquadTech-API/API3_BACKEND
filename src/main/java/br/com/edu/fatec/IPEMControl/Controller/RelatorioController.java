package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.RelatorioDiarioDTO;
import br.com.edu.fatec.IPEMControl.Service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/diario")
    public ResponseEntity<RelatorioDiarioDTO> relatorioDiario(
            @RequestParam Integer matricula,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        RelatorioDiarioDTO relatorio = relatorioService.gerarRelatorioDiarioPorTecnico(matricula, data);
        return ResponseEntity.ok(relatorio);
    }
}