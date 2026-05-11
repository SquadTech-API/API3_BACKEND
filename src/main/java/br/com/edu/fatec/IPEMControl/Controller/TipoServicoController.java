package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.Entities.ServiceType;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Repository.TipoServicoRepository;
import br.com.edu.fatec.IPEMControl.Service.VeiculoServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CORRIGIDO: antes só tinha GET /type-services.
 * Adicionados todos os endpoints que o frontend chama:
 * - GET  /type-services/ativos              — serviços habilitados (para selects)
 * - GET  /type-services/vehicle/{id}/ativos — serviços habilitados para um veículo
 * - POST /type-services                     — cadastrar type de serviço
 * - PUT  /type-services/{id}               — editar type de serviço
 * - PATCH /type-services/{id}/toggle       — habilitar/desabilitar serviço
 */
@RestController
@RequestMapping("/tipo-servicos")
@CrossOrigin(origins = "*")
public class TipoServicoController {

    @Autowired
    private TipoServicoRepository tipoServicoRepository;

    @Autowired
    private VeiculoServicoService veiculoServicoService;

    // GET /type-services — listar todos
    @GetMapping
    public ResponseEntity<List<ServiceType>> listar() {
        return ResponseEntity.ok(tipoServicoRepository.findAll());
    }

    // GET /type-services/ativos — NOVO: apenas os habilitados
    @GetMapping("/ativos")
    public ResponseEntity<List<ServiceType>> listarAtivos() {
        return ResponseEntity.ok(tipoServicoRepository.findByHabilitadoTrue());
    }

    // GET /type-services/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ServiceType> buscar(@PathVariable Integer id) {
        return tipoServicoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /type-services/vehicle/{vehicleId}/ativos — NOVO
    // Retorna serviços habilitados para um veículo específico
    @GetMapping("/veiculo/{idVeiculo}/ativos")
    public ResponseEntity<List<ServiceType>> listarAtivosDoVeiculo(@PathVariable Integer idVeiculo) {
        return ResponseEntity.ok(veiculoServicoService.listarServicosAtivosDoVeiculo(idVeiculo));
    }

    // POST /type-services — NOVO: cadastrar
    @PostMapping
    public ResponseEntity<ServiceType> criar(@RequestBody ServiceType serviceType) {
        serviceType.setLicensed(true); // padrão ao criar
        return ResponseEntity.status(201).body(tipoServicoRepository.save(serviceType));
    }

    // PUT /type-services/{id} — NOVO: editar
    @PutMapping("/{id}")
    public ResponseEntity<ServiceType> atualizar(
            @PathVariable Integer id,
            @RequestBody ServiceType atualizado) {

        ServiceType existente = tipoServicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de serviço não encontrado."));

        if (atualizado.getNomeServico() != null && !atualizado.getNomeServico().isBlank())
            existente.setNomeServico(atualizado.getNomeServico());
        if (atualizado.getDescription() != null)
            existente.setDescription(atualizado.getDescription());
        if (atualizado.getOilChangeST() != null)
            existente.setOilChangeST(atualizado.getOilChangeST());

        return ResponseEntity.ok(tipoServicoRepository.save(existente));
    }

    // PATCH /type-services/{id}/toggle — NOVO: habilitar/desabilitar
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ServiceType> toggle(@PathVariable Integer id) {
        ServiceType ts = tipoServicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de serviço não encontrado."));
        ts.setLicensed(!Boolean.TRUE.equals(ts.getLicensed()));
        return ResponseEntity.ok(tipoServicoRepository.save(ts));
    }
}