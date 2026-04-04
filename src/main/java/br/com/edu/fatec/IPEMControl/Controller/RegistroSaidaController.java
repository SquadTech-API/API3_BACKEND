package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.FecharSaidaDTO;
import br.com.edu.fatec.IPEMControl.DTO.RegistroSaidaDTO;
import br.com.edu.fatec.IPEMControl.DTO.RetornoDTO;
import br.com.edu.fatec.IPEMControl.DTO.RetornoRespostaDTO;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import br.com.edu.fatec.IPEMControl.Service.RegistroSaidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registro-saidas")
public class RegistroSaidaController {

    @Autowired
    private RegistroSaidaService registroSaidaService;

    @Autowired
    private RegistroSaidaRepository registroSaidaRepository;

    @PostMapping
    public ResponseEntity<RegistroSaida> abrirSaida(@RequestBody RegistroSaidaDTO dto) {
        return ResponseEntity.status(201).body(registroSaidaService.abrirSaida(dto));
    }

    @PatchMapping("/{id}/retorno")
    public ResponseEntity<RetornoRespostaDTO> registrarRetorno(
            @PathVariable Integer id,
            @RequestBody RetornoDTO dto) {
        return ResponseEntity.ok(registroSaidaService.registrarRetorno(id, dto));
    }

    @PatchMapping("/{id}/fechar")
    public ResponseEntity<RegistroSaida> fecharSaida(
            @PathVariable Integer id,
            @RequestBody FecharSaidaDTO dto) {
        return ResponseEntity.ok(registroSaidaService.fecharSaida(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<RegistroSaida>> listar() {
        return ResponseEntity.ok(registroSaidaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroSaida> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(registroSaidaService.buscarPorId(id));
    }

    /**
     * Busca saída em andamento de um veículo específico.
     * GET /registro-saidas/ativo?veiculoId={id}
     */
    @GetMapping("/ativo")
    public ResponseEntity<RegistroSaida> buscarSaidaAtivaPorVeiculo(@RequestParam Integer veiculoId) {
        return registroSaidaRepository
                .findTopByVeiculoIdVeiculoAndStatusOrderByDataHoraSaidaDesc(veiculoId, "em_andamento")
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca saída em andamento do usuário logado pela matrícula.
     * Usado pelo veiculos.js ao carregar: se o usuário já tem saída ativa,
     * redireciona automaticamente para nova_entrada.html.
     * GET /registro-saidas/ativo-usuario?matricula={matricula}
     * Retorna 404 se não houver saída ativa (comportamento esperado e tratado no JS).
     */
    @GetMapping("/ativo-usuario")
    public ResponseEntity<RegistroSaida> buscarSaidaAtivaPorUsuario(@RequestParam Integer matricula) {
        return registroSaidaRepository
                .findTopByUsuarioMatriculaAndStatusOrderByDataHoraSaidaDesc(matricula, "em_andamento")
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}