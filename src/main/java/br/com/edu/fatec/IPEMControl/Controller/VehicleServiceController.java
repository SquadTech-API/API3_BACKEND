package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.Service.VeiculoServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * NOVO: Controller para vínculo veículo ↔ serviço.
 * Antes não existia — o frontend chamava /vehicle-servico/sincronizar e recebia 404.
 */
@RestController
@RequestMapping("/vehicle-service")
@CrossOrigin(origins = "*")
public class VehicleServiceController {

    @Autowired
    private VeiculoServicoService vehicleServiceService;

    /**
     * Sincroniza os serviços habilitados para um veículo.
     * Remove todos os vínculos atuais e cria apenas os enviados.
     * POST /vehicle-servico/sincronizar/{vehicleId}
     * Body: [1, 2, 5, 8] — lista de IDs de tipo_servico
     */
    @PostMapping("/sync/{vehicleId}")
    public ResponseEntity<Void> sync(
            @PathVariable Integer vehicleId,
            @RequestBody List<Integer> serviceTypeIds) {
        vehicleServiceService.sincronizar(vehicleId, serviceTypeIds);
        return ResponseEntity.ok().build();
    }
}