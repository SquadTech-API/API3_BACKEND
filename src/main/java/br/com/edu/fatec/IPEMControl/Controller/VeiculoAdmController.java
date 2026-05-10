package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
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
    public Vehicle cadastrar(@RequestBody Vehicle vehicle) {
        return repository.save(vehicle);
    }

    @GetMapping
    public List<Vehicle> listarTodos() {
        return repository.findAll();
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Integer id) {
        repository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Vehicle editar(@PathVariable Integer id, @RequestBody Vehicle atualizado) {
        return repository.findById(id)
                .map(v -> {
                    v.setLicensePlate(atualizado.getLicensePlate());
                    v.setBrand(atualizado.getBrand());
                    v.setModel(atualizado.getModel());
                    v.setYear(atualizado.getYear());
                    v.setCurrentKm(atualizado.getCurrentKm());
                    v.setFuelType(atualizado.getFuelType());
                    v.setAvailable(atualizado.getAvailable());
                    v.setPrefix(atualizado.getPrefix());
                    v.setNucleoDar(atualizado.getNucleoDar());
                    v.setLicenseCategory(atualizado.getLicenseCategory());
                    return repository.save(v);
                }).orElseGet(() -> {
                    atualizado.setVehicleId(id);
                    return repository.save(atualizado);
                });
    }
}