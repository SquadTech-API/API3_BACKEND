package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.ServiceTypeDTO;
import br.com.edu.fatec.ipemControl.entity.ServiceType;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;

    public List<ServiceTypeDTO> findAll() {
        return serviceTypeRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<ServiceTypeDTO> findEnabled() {
        return serviceTypeRepository.findByEnabledTrue().stream().map(this::toDTO).toList();
    }

    public ServiceTypeDTO findById(Integer id) {
        return serviceTypeRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de serviço não encontrado."));
    }

    public ServiceTypeDTO create(ServiceTypeDTO dto) {
        ServiceType serviceType = new ServiceType();
        apply(dto, serviceType);
        if (!serviceType.isEnabled()) serviceType.setEnabled(true);
        return toDTO(serviceTypeRepository.save(serviceType));
    }

    public ServiceTypeDTO update(Integer id, ServiceTypeDTO dto) {
        ServiceType serviceType = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de serviço não encontrado."));
        apply(dto, serviceType);
        return toDTO(serviceTypeRepository.save(serviceType));
    }

    public ServiceTypeDTO toggle(Integer id) {
        ServiceType serviceType = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de serviço não encontrado."));
        serviceType.setEnabled(!serviceType.isEnabled());
        return toDTO(serviceTypeRepository.save(serviceType));
    }

    public ServiceTypeDTO toDTO(ServiceType serviceType) {
        ServiceTypeDTO dto = new ServiceTypeDTO();
        dto.setId(serviceType.getId());
        dto.setServiceName(serviceType.getServiceName());
        dto.setDescription(serviceType.getDescription());
        dto.setEnabled(serviceType.isEnabled());
        dto.setOilChange(serviceType.isOilChange());
        return dto;
    }

    private void apply(ServiceTypeDTO dto, ServiceType serviceType) {
        if (dto.getServiceName() != null) serviceType.setServiceName(dto.getServiceName());
        if (dto.getDescription() != null) serviceType.setDescription(dto.getDescription());
        if (dto.getEnabled() != null) serviceType.setEnabled(dto.getEnabled());
        if (dto.getOilChange() != null) serviceType.setOilChange(dto.getOilChange());
    }
}