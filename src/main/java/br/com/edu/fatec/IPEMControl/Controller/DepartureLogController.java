package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.*;
import br.com.edu.fatec.IPEMControl.Entities.DepartureLog;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import br.com.edu.fatec.IPEMControl.Service.DepartureLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registro-saidas")
@CrossOrigin(origins = "*")
public class DepartureLogController {

    @Autowired
    private DepartureLogService departureLogService;

    @Autowired
    private RegistroSaidaRepository registroSaidaRepository;

    @PostMapping
    public ResponseEntity<DepartureLog> abrirSaida(@RequestBody DepartureLogDTO dto) {
        return ResponseEntity.status(201).body(departureLogService.abrirSaida(dto));
    }

    @PatchMapping("/{id}/retorno")
    public ResponseEntity<ReturnResponseDTO> registrarRetorno(
            @PathVariable Integer id,
            @RequestBody ReturnDTO dto) {
        return ResponseEntity.ok(departureLogService.registrarRetorno(id, dto));
    }

    @PatchMapping("/{id}/fechar")
    public ResponseEntity<DepartureLog> fecharSaida(
            @PathVariable Integer id,
            @RequestBody CloseExitDTO dto) {
        return ResponseEntity.ok(departureLogService.fecharSaida(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<DepartureLog>> listar() {
        return ResponseEntity.ok(departureLogService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartureLog> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(departureLogService.buscarPorId(id));
    }

    // Busca saída ativa de um veículo
    @GetMapping("/ativo")
    public ResponseEntity<DepartureLog> buscarSaidaAtivaPorVeiculo(@RequestParam Integer veiculoId) {
        return registroSaidaRepository
                .findTopByVeiculoIdVeiculoAndStatusOrderByDataHoraSaidaDesc(veiculoId, "em_andamento")
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Busca saída ativa do usuário — usado pelo vehicles.js para redirecionamento automático
    @GetMapping("/ativo-usuario")
    public ResponseEntity<DepartureLog> buscarSaidaAtivaPorUsuario(@RequestParam Integer matricula) {
        return registroSaidaRepository
                .findTopByUsuarioMatriculaAndStatusOrderByDataHoraSaidaDesc(matricula, "em_andamento")
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // NOVO: saídas de troca de óleo por veículo — usado pelo editar-troca-de-oleo.js
    @GetMapping("/veiculo/{idVeiculo}/troca-oleo")
    public ResponseEntity<List<DepartureLog>> buscarSaidasTrocaOleo(@PathVariable Integer idVeiculo) {
        return ResponseEntity.ok(
                registroSaidaRepository.findByVeiculoIdVeiculoAndTipoServicoEhTrocaOleoTrue(idVeiculo)
        );
    }
}