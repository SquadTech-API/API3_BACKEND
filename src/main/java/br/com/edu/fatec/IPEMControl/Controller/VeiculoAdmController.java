package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.Entities.Veiculo;
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
    public Veiculo cadastrar(@RequestBody Veiculo vehicle) {
        return repository.save(vehicle);
    }

    @GetMapping
    public ResponseEntity<List<Veiculo>> listarTodos() {
        List<Veiculo> lista = repository.findAll();
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Integer id) {
        repository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Veiculo editar(@PathVariable Integer id, @RequestBody Veiculo vehicleAtualizado) {
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