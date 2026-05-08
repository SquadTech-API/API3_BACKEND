package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.FecharSaidaDTO;
import br.com.edu.fatec.IPEMControl.DTO.RegistroSaidaDTO;
import br.com.edu.fatec.IPEMControl.DTO.RetornoDTO;
import br.com.edu.fatec.IPEMControl.DTO.RetornoRespostaDTO;
import br.com.edu.fatec.IPEMControl.Entities.DepartureLog;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import br.com.edu.fatec.IPEMControl.Service.RegistroSaidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CORRIGIDO: adicionado endpoint para saídas com troca de óleo por veículo.
 * Usado pelo editar-troca-de-oleo.js ao popular o select de saídas.
 */
@RestController
@RequestMapping("/registro-saidas")
@CrossOrigin(origins = "*")
public class RegistroSaidaController {

    @Autowired
    private RegistroSaidaService registroSaidaService;

    @Autowired
    private RegistroSaidaRepository registroSaidaRepository;

    @PostMapping
    public ResponseEntity<DepartureLog> abrirSaida(@RequestBody RegistroSaidaDTO dto) {
        return ResponseEntity.status(201).body(registroSaidaService.abrirSaida(dto));
    }

    @PatchMapping("/{id}/retorno")
    public ResponseEntity<RetornoRespostaDTO> registrarRetorno(
            @PathVariable Integer id,
            @RequestBody RetornoDTO dto) {
        return ResponseEntity.ok(registroSaidaService.registrarRetorno(id, dto));
    }

    @PatchMapping("/{id}/fechar")
    public ResponseEntity<DepartureLog> fecharSaida(
            @PathVariable Integer id,
            @RequestBody FecharSaidaDTO dto) {
        return ResponseEntity.ok(registroSaidaService.fecharSaida(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<DepartureLog>> listar() {
        return ResponseEntity.ok(registroSaidaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartureLog> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(registroSaidaService.buscarPorId(id));
    }

    // Busca saída ativa de um veículo
    @GetMapping("/ativo")
    public ResponseEntity<DepartureLog> buscarSaidaAtivaPorVeiculo(@RequestParam Integer veiculoId) {
        return registroSaidaRepository
                .findTopByVeiculoIdVeiculoAndStatusOrderByDataHoraSaidaDesc(veiculoId, "em_andamento")
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Busca saída ativa do usuário — usado pelo veiculos.js para redirecionamento automático
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