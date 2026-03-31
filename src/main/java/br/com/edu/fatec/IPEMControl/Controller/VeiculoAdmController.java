package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.Entities.Veiculo;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Adicione este import
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adm/veiculos")
@CrossOrigin("*")
public class VeiculoAdmController {

    @Autowired
    private VeiculoRepository repository;

    @PostMapping
    public Veiculo cadastrar(@RequestBody Veiculo veiculo) {
        return repository.save(veiculo);
    }

    // Mantive apenas um GetMapping para não dar erro
    @GetMapping
    public ResponseEntity<List<Veiculo>> listarTodos() {
        List<Veiculo> lista = repository.findAll();
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Veiculo editar(@PathVariable Long id, @RequestBody Veiculo veiculoAtualizado) {
        return repository.findById(id)
                .map(veiculo -> {
                    veiculo.setPlaca(veiculoAtualizado.getPlaca());
                    veiculo.setMarca(veiculoAtualizado.getMarca());
                    veiculo.setModelo(veiculoAtualizado.getModelo());
                    veiculo.setAno(veiculoAtualizado.getAno());
                    veiculo.setKm(veiculoAtualizado.getKm());
                    veiculo.setCombustivel(veiculoAtualizado.getCombustivel());
                    veiculo.setStatus(veiculoAtualizado.getStatus());
                    return repository.save(veiculo);
                }).orElseGet(() -> {
                    veiculoAtualizado.setId(id);
                    return repository.save(veiculoAtualizado);
                });
    }
}