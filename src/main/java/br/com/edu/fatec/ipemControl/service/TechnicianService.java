package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.entities.Technician;
import br.com.edu.fatec.ipemControl.repository.TechnicianRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TechnicianService {

    private final TechnicianRepository repository;

    public TechnicianService(TechnicianRepository repository) {
        this.repository = repository;
    }

    public Technician save(Technician motorista) {
        return repository.save(motorista);
    }

    public List<Technician> findAll() {
        return repository.findAll();
    }

    public Optional<Technician> findById(Long id) {
        return repository.findById(id);
    }

    public Technician update(Long id, Technician updatedTechnician) {
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