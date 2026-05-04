package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.Entities.TipoServico;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Repository.TipoServicoRepository;
import br.com.edu.fatec.IPEMControl.Service.VeiculoServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CORRIGIDO: antes só tinha GET /tipo-servicos.
 * Adicionados todos os endpoints que o frontend chama:
 * - GET  /tipo-servicos/ativos              — serviços habilitados (para selects)
 * - GET  /tipo-servicos/veiculo/{id}/ativos — serviços habilitados para um veículo
 * - POST /tipo-servicos                     — cadastrar tipo de serviço
 * - PUT  /tipo-servicos/{id}               — editar tipo de serviço
 * - PATCH /tipo-servicos/{id}/toggle       — habilitar/desabilitar serviço
 */
@RestController
@RequestMapping("/tipo-servicos")
@CrossOrigin(origins = "*")
public class TipoServicoController {

    @Autowired
    private TipoServicoRepository tipoServicoRepository;

    @Autowired
    private VeiculoServicoService veiculoServicoService;

    // GET /tipo-servicos — listar todos
    @GetMapping
    public ResponseEntity<List<TipoServico>> listar() {
        return ResponseEntity.ok(tipoServicoRepository.findAll());
    }

    // GET /tipo-servicos/ativos — NOVO: apenas os habilitados
    @GetMapping("/ativos")
    public ResponseEntity<List<TipoServico>> listarAtivos() {
        return ResponseEntity.ok(tipoServicoRepository.findByHabilitadoTrue());
    }

    // GET /tipo-servicos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TipoServico> buscar(@PathVariable Integer id) {
        return tipoServicoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /tipo-servicos/veiculo/{idVeiculo}/ativos — NOVO
    // Retorna serviços habilitados para um veículo específico
    @GetMapping("/veiculo/{idVeiculo}/ativos")
    public ResponseEntity<List<TipoServico>> listarAtivosDoVeiculo(@PathVariable Integer idVeiculo) {
        return ResponseEntity.ok(veiculoServicoService.listarServicosAtivosDoVeiculo(idVeiculo));
    }

    // POST /tipo-servicos — NOVO: cadastrar
    @PostMapping
    public ResponseEntity<TipoServico> criar(@RequestBody TipoServico tipoServico) {
        tipoServico.setHabilitado(true); // padrão ao criar
        return ResponseEntity.status(201).body(tipoServicoRepository.save(tipoServico));
    }

    // PUT /tipo-servicos/{id} — NOVO: editar
    @PutMapping("/{id}")
    public ResponseEntity<TipoServico> atualizar(
            @PathVariable Integer id,
            @RequestBody TipoServico atualizado) {

        TipoServico existente = tipoServicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de serviço não encontrado."));

        if (atualizado.getNomeServico() != null && !atualizado.getNomeServico().isBlank())
            existente.setNomeServico(atualizado.getNomeServico());
        if (atualizado.getDescricao() != null)
            existente.setDescricao(atualizado.getDescricao());
        if (atualizado.getEhTrocaOleo() != null)
            existente.setEhTrocaOleo(atualizado.getEhTrocaOleo());

        return ResponseEntity.ok(tipoServicoRepository.save(existente));
    }

    // PATCH /tipo-servicos/{id}/toggle — NOVO: habilitar/desabilitar
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TipoServico> toggle(@PathVariable Integer id) {
        TipoServico ts = tipoServicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de serviço não encontrado."));
        ts.setHabilitado(!Boolean.TRUE.equals(ts.getHabilitado()));
        return ResponseEntity.ok(tipoServicoRepository.save(ts));
    }
}