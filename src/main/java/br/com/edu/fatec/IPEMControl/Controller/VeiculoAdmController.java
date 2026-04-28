package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adm/veiculos")
@CrossOrigin(origins = "*")
public class VeiculoAdmController {

    @Autowired
    private VeiculoRepository repository;

    @PostMapping
    public Vehicle cadastrar(@RequestBody Vehicle vehicle) {
        return repository.save(vehicle);
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> listarTodos() {
        List<Vehicle> lista = repository.findAll();
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Integer id) {
        repository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Vehicle editar(@PathVariable Integer id, @RequestBody Vehicle vehicleAtualizado) {
        return repository.findById(id)
                .map(veiculo -> {
                    veiculo.setPlaca(vehicleAtualizado.getPlaca());
                    veiculo.setMarca(vehicleAtualizado.getMarca());
                    veiculo.setModelo(vehicleAtualizado.getModelo());
                    veiculo.setAno(vehicleAtualizado.getAno());
                    veiculo.setKmAtual(vehicleAtualizado.getKmAtual());
                    veiculo.setTipoCombustivel(vehicleAtualizado.getTipoCombustivel());
                    veiculo.setDisponivel(vehicleAtualizado.getDisponivel());
                    veiculo.setPrefixo(vehicleAtualizado.getPrefixo());
                    veiculo.setNucleoDar(vehicleAtualizado.getNucleoDar());
                    veiculo.setHabilitacaoCategoria(vehicleAtualizado.getHabilitacaoCategoria());
                    return repository.save(veiculo);
                }).orElseGet(() -> {
                    vehicleAtualizado.setIdVeiculo(id);
                    return repository.save(vehicleAtualizado);
                });
    }
}