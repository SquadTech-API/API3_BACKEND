package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.FuelTypeDTO;
import br.com.edu.fatec.ipemControl.entity.FuelType;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.FuelTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuelTypeService {

    private final FuelTypeRepository fuelTypeRepository;

    // ── GET /fuel-types ───────────────────────────────────────────
    public List<FuelTypeDTO> findAll() {
        return fuelTypeRepository.findAll().stream().map(this::toDTO).toList();
    }

    // ── GET /fuel-types/active ────────────────────────────────────
    public List<FuelTypeDTO> findActive() {
        return fuelTypeRepository.findByActiveTrue().stream().map(this::toDTO).toList();
    }

    // ── GET /fuel-types/{id} ──────────────────────────────────────
    public FuelTypeDTO findById(Integer id) {
        return fuelTypeRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de combustível não encontrado."));
    }

    // ── POST /fuel-types (#A14) ───────────────────────────────────
    public FuelTypeDTO create(FuelTypeDTO dto) {
        FuelType fuelType = new FuelType();
        apply(dto, fuelType);
        fuelType.setActive(true);
        return toDTO(fuelTypeRepository.save(fuelType));
    }

    // ── PUT /fuel-types/{id} ──────────────────────────────────────
    public FuelTypeDTO update(Integer id, FuelTypeDTO dto) {
        FuelType fuelType = fuelTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de combustível não encontrado."));
        apply(dto, fuelType);
        return toDTO(fuelTypeRepository.save(fuelType));
    }

    // ── PATCH /fuel-types/{id}/toggle ────────────────────────────
    public FuelTypeDTO toggle(Integer id) {
        FuelType fuelType = fuelTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de combustível não encontrado."));
        fuelType.setActive(!fuelType.isActive());
        return toDTO(fuelTypeRepository.save(fuelType));
    }

    // ── Helpers ───────────────────────────────────────────────────
    private void apply(FuelTypeDTO dto, FuelType fuelType) {
        if (dto.getName()         != null) fuelType.setName(dto.getName());
        if (dto.getAbbreviation() != null) fuelType.setAbbreviation(dto.getAbbreviation());
        if (dto.getCategory()     != null) fuelType.setCategory(dto.getCategory());
        if (dto.getPricePerLiter()!= null) fuelType.setPricePerLiter(dto.getPricePerLiter());
    }

    public FuelTypeDTO toDTO(FuelType f) {
        FuelTypeDTO dto = new FuelTypeDTO();
        dto.setId(f.getId());
        dto.setName(f.getName());
        dto.setAbbreviation(f.getAbbreviation());
        dto.setCategory(f.getCategory());
        dto.setPricePerLiter(f.getPricePerLiter());
        dto.setActive(f.isActive());
        return dto;
    }
}