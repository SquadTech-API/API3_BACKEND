package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.Entities.Technician;
import br.com.edu.fatec.IPEMControl.Repository.TechnicianRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TecnicoService {

    private final TechnicianRepository repository;

    public TecnicoService(TechnicianRepository repository) {
        this.repository = repository;
    }

    public Technician salvar(Technician motorista) {
        return repository.save(motorista);
    }

    public List<Technician> listarTodos() {
        return repository.findAll();
    }

    public Optional<Technician> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Technician atualizar(Long id, Technician updatedTechnician) {
        return repository.findById(id).map(m -> {
            m.setName(updatedTechnician.getName());
            m.setDriveLicense(updatedTechnician.getDriveLicense());
            m.setPhone(updatedTechnician.getPhone());
            return repository.save(m);
        }).orElseThrow(() -> new RuntimeException("Técnico não encontrado"));
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}