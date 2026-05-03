package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.RelatorioDiarioDTO;
import br.com.edu.fatec.IPEMControl.Service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller // Alterado de @RestController para @Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/diario")
    public String relatorioDiario(
            @RequestParam Integer matricula,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            Model model) { // Adicionado o Model aqui

        // 1. Busca os dados no serviço
        RelatorioDiarioDTO relatorio = relatorioService.gerarRelatorioDiarioPorTecnico(matricula, data);

        // 2. Coloca os dados dentro do "pacote" que o HTML vai abrir
        model.addAttribute("relatorio", relatorio);

        // 3. Retorna o NOME do arquivo HTML que está na pasta templates (sem o .html)
        // Se o seu arquivo se chamar "relatorio-viatura.html", coloque apenas "relatorio-viatura"
        return "relatorio-viatura";
    }
}