package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.VeiculoResumoDTO;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import br.com.edu.fatec.IPEMControl.Service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @Autowired
    private VeiculoRepository veiculoRepository;

    // Lista resumida para a tela de cards
    @GetMapping
    public ResponseEntity<List<VeiculoResumoDTO>> listar() {
        return ResponseEntity.ok(veiculoService.listarVeiculosResumido());
    }

    // ADICIONADO: busca veículo completo por ID
    // Usado pelo nova_saida.js e nova_entrada.js para preencher o card de veículo
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> buscarPorId(@PathVariable Integer id) {
        return veiculoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Cadastro de novo veículo (usado pelo addveic.js)
    @PostMapping
    public ResponseEntity<Vehicle> criar(@RequestBody Vehicle vehicle) {
        return ResponseEntity.status(201).body(veiculoRepository.save(vehicle));
    }
}