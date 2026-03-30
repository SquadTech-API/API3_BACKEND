package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.Entities.Veiculo;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public List<Veiculo> listar() {
        return repository.findAll();
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
                    veiculo.setModelo(veiculoAtualizado.getModelo());
                    // Adicione aqui outros campos (cor, ano, etc) se existirem no seu Veiculo.java
                    return repository.save(veiculo);
                }).orElseGet(() -> {
                    veiculoAtualizado.setId(id);
                    return repository.save(veiculoAtualizado);
                });
    }

}