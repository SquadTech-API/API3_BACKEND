package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.Entities.Tecnico;
import br.com.edu.fatec.IPEMControl.Repository.TecnicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TecnicoService {

    private final TecnicoRepository repository;

    public TecnicoService(TecnicoRepository repository) {
        this.repository = repository;
    }

    public Tecnico salvar(Tecnico motorista) {
        return repository.save(motorista);
    }

    public List<Tecnico> listarTodos() {
        return repository.findAll();
    }

    public Optional<Tecnico> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Tecnico atualizar(Long id, Tecnico tecnicoAtualizado) {
        return repository.findById(id).map(m -> {
            m.setNome(tecnicoAtualizado.getNome());
            m.setCnh(tecnicoAtualizado.getCnh());
            m.setTelefone(tecnicoAtualizado.getTelefone());
            return repository.save(m);
        }).orElseThrow(() -> new RuntimeException("Técnico não encontrado"));
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}