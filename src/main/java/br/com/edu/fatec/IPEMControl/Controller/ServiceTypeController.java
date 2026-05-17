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
@RequestMapping("/service-types")
@CrossOrigin(origins = "*")
public class ServiceTypeController {

    @Autowired
    private TipoServicoRepository serviceTypeRepository;

    @Autowired
    private VeiculoServicoService vehicleServiceService;

    // GET /type-services — listar todos
    @GetMapping
    public ResponseEntity<List<ServiceType>> findAll() {
        return ResponseEntity.ok(serviceTypeRepository.findAll());
    }

    // GET /type-services/ativos — NOVO: apenas os habilitados
    @GetMapping("/active")
    public ResponseEntity<List<ServiceType>> findActive() {
        return ResponseEntity.ok(serviceTypeRepository.findByHabilitadoTrue());
    }

    // GET /type-services/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ServiceType> findById(@PathVariable Integer id) {
        return serviceTypeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /type-services/vehicle/{vehicleId}/ativos — NOVO
    // Retorna serviços habilitados para um veículo específico
    @GetMapping("/vehicle/{vehicleId}/active")
    public ResponseEntity<List<ServiceType>> findActiveByVehicle(@PathVariable Integer vehicleId) {
        return ResponseEntity.ok(vehicleServiceService.listarServicosAtivosDoVeiculo(vehicleId));
    }

    // POST /type-services — NOVO: cadastrar
    @PostMapping
    public ResponseEntity<ServiceType> create(@RequestBody ServiceType serviceType) {
        serviceType.setLicensed(true); // padrão ao criar
        return ResponseEntity.status(201).body(serviceTypeRepository.save(serviceType));
    }

    // PUT /type-services/{id} — NOVO: editar
    @PutMapping("/{id}")
    public ResponseEntity<ServiceType> update(
            @PathVariable Integer id,
            @RequestBody ServiceType updatedServiceType) {

        ServiceType existingServiceType = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de serviço não encontrado."));

        if (updatedServiceType.getNomeServico() != null && !updatedServiceType.getNomeServico().isBlank())
            existingServiceType.setNomeServico(updatedServiceType.getNomeServico());
        if (updatedServiceType.getDescription() != null)
            existingServiceType.setDescription(updatedServiceType.getDescription());
        if (updatedServiceType.getOilChangeST() != null)
            existingServiceType.setOilChangeST(updatedServiceType.getOilChangeST());

        return ResponseEntity.ok(serviceTypeRepository.save(existingServiceType));
    }

    // PATCH /type-services/{id}/toggle — NOVO: habilitar/desabilitar
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ServiceType> toggle(@PathVariable Integer id) {
        ServiceType serviceType = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de serviço não encontrado."));
        serviceType.setLicensed(!Boolean.TRUE.equals(serviceType.getLicensed()));
        return ResponseEntity.ok(serviceTypeRepository.save(serviceType));
    }
}