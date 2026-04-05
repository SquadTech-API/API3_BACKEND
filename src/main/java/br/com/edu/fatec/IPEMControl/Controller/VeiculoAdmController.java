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
    public Veiculo cadastrar(@RequestBody Veiculo veiculo) {
        return repository.save(veiculo);
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
    public Veiculo editar(@PathVariable Integer id, @RequestBody Veiculo veiculoAtualizado) {
        return repository.findById(id)
                .map(veiculo -> {
                    veiculo.setPlaca(veiculoAtualizado.getPlaca());
                    veiculo.setMarca(veiculoAtualizado.getMarca());
                    veiculo.setModelo(veiculoAtualizado.getModelo());
                    veiculo.setAno(veiculoAtualizado.getAno());
                    veiculo.setKmAtual(veiculoAtualizado.getKmAtual());
                    veiculo.setTipoCombustivel(veiculoAtualizado.getTipoCombustivel());
                    veiculo.setDisponivel(veiculoAtualizado.getDisponivel());
                    veiculo.setPrefixo(veiculoAtualizado.getPrefixo());
                    veiculo.setNucleoDar(veiculoAtualizado.getNucleoDar());
                    veiculo.setHabilitacaoCategoria(veiculoAtualizado.getHabilitacaoCategoria());
                    return repository.save(veiculo);
                }).orElseGet(() -> {
                    veiculoAtualizado.setIdVeiculo(id);
                    return repository.save(veiculoAtualizado);
                });
    }
}