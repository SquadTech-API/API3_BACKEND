package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.Entities.Motorista;
import br.com.edu.fatec.IPEMControl.Repository.MotoristaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MotoristaService {

    private final MotoristaRepository repository;

    public MotoristaService(MotoristaRepository repository) {
        this.repository = repository;
    }

    public Motorista salvar(Motorista motorista) {
        return repository.save(motorista);
    }

    public List<Motorista> listarTodos() {
        return repository.findAll();
    }

    public Optional<Motorista> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Motorista atualizar(Long id, Motorista motoristaAtualizado) {
        return repository.findById(id).map(m -> {
            m.setNome(motoristaAtualizado.getNome());
            m.setCnh(motoristaAtualizado.getCnh());
            m.setTelefone(motoristaAtualizado.getTelefone());
            return repository.save(m);
        }).orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}