package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/vehicles")
@CrossOrigin(origins = "*")
public class VehicleAdminController {

    private final VeiculoRepository vehicleRepository;

    public VehicleAdminController(VeiculoRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @PostMapping
    public Vehicle register(@RequestBody Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @GetMapping
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        vehicleRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Vehicle update(@PathVariable Integer id, @RequestBody Vehicle updatedVehicle) {
        return vehicleRepository.findById(id)
                .map(v -> {
                    v.setLicensePlate(updatedVehicle.getLicensePlate());
                    v.setBrand(updatedVehicle.getBrand());
                    v.setModel(updatedVehicle.getModel());
                    v.setYear(updatedVehicle.getYear());
                    v.setCurrentKm(updatedVehicle.getCurrentKm());
                    v.setFuelType(updatedVehicle.getFuelType());
                    v.setAvailable(updatedVehicle.getAvailable());
                    v.setPrefix(updatedVehicle.getPrefix());
                    v.setNucleoDar(updatedVehicle.getNucleoDar());
                    v.setLicenseCategory(updatedVehicle.getLicenseCategory());
                    return vehicleRepository.save(v);
                }).orElseGet(() -> {
                    updatedVehicle.setVehicleId(id);
                    return vehicleRepository.save(updatedVehicle);
                });
    }
}