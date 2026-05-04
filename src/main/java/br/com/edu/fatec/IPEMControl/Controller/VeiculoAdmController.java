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

    private final VeiculoRepository repository;

    public VeiculoAdmController(VeiculoRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Veiculo cadastrar(@RequestBody Veiculo veiculo) {
        return repository.save(veiculo);
    }

    @GetMapping
    public List<Veiculo> listarTodos() {
        return repository.findAll();
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Integer id) {
        repository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Veiculo editar(@PathVariable Integer id, @RequestBody Veiculo atualizado) {
        return repository.findById(id)
                .map(v -> {
                    v.setPlaca(atualizado.getPlaca());
                    v.setMarca(atualizado.getMarca());
                    v.setModelo(atualizado.getModelo());
                    v.setAno(atualizado.getAno());
                    v.setKmAtual(atualizado.getKmAtual());
                    v.setTipoCombustivel(atualizado.getTipoCombustivel());
                    v.setDisponivel(atualizado.getDisponivel());
                    v.setPrefixo(atualizado.getPrefixo());
                    v.setNucleoDar(atualizado.getNucleoDar());
                    v.setHabilitacaoCategoria(atualizado.getHabilitacaoCategoria());
                    return repository.save(v);
                }).orElseGet(() -> {
                    atualizado.setIdVeiculo(id);
                    return repository.save(atualizado);
                });
    }
}