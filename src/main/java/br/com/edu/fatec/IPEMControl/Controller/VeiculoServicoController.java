package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.Service.VeiculoServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * NOVO: Controller para vínculo veículo ↔ serviço.
 * Antes não existia — o frontend chamava /veiculo-servico/sincronizar e recebia 404.
 */
@RestController
@RequestMapping("/veiculo-servico")
@CrossOrigin(origins = "*")
public class VeiculoServicoController {

    @Autowired
    private VeiculoServicoService veiculoServicoService;

    /**
     * Sincroniza os serviços habilitados para um veículo.
     * Remove todos os vínculos atuais e cria apenas os enviados.
     * POST /veiculo-servico/sincronizar/{idVeiculo}
     * Body: [1, 2, 5, 8] — lista de IDs de tipo_servico
     */
    @PostMapping("/sincronizar/{idVeiculo}")
    public ResponseEntity<Void> sincronizar(
            @PathVariable Integer idVeiculo,
            @RequestBody List<Integer> idsTipoServico) {
        veiculoServicoService.sincronizar(idVeiculo, idsTipoServico);
        return ResponseEntity.ok().build();
    }
}