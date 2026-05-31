package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.ServiceTypeDTO;
import br.com.edu.fatec.ipemControl.entity.ServiceType;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.ServiceTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;

    public ServiceTypeService(ServiceTypeRepository serviceTypeRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
    }

    public List<ServiceTypeDTO> findAll() {
        return serviceTypeRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<ServiceTypeDTO> findActive() {
        return serviceTypeRepository.findByLicensedTrue().stream().map(this::toDTO).toList();
    }

    public ServiceTypeDTO findById(Integer id) {
        return serviceTypeRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Service type not found."));
    }

    public ServiceTypeDTO create(ServiceTypeDTO dto) {
        ServiceType serviceType = new ServiceType();
        apply(dto, serviceType);
        if (serviceType.getLicensed() == null) serviceType.setLicensed(true);
        return toDTO(serviceTypeRepository.save(serviceType));
    }

    public ServiceTypeDTO update(Integer id, ServiceTypeDTO dto) {
        ServiceType serviceType = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service type not found."));
        apply(dto, serviceType);
        return toDTO(serviceTypeRepository.save(serviceType));
    }

    public ServiceTypeDTO toggle(Integer id) {
        ServiceType serviceType = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service type not found."));
        serviceType.setLicensed(!Boolean.TRUE.equals(serviceType.getLicensed()));
        return toDTO(serviceTypeRepository.save(serviceType));
    }

    public ServiceTypeDTO toDTO(ServiceType serviceType) {
        ServiceTypeDTO dto = new ServiceTypeDTO();
        dto.setServiceTypeId(serviceType.getServiceTypeId());
        dto.setServiceName(serviceType.getServiceName());
        dto.setDescription(serviceType.getDescription());
        dto.setLicensed(serviceType.getLicensed());
        dto.setOilChange(serviceType.getOilChangeST());
        return dto;
    }

    private void apply(ServiceTypeDTO dto, ServiceType serviceType) {
        if (dto.getServiceName() != null) serviceType.setServiceName(dto.getServiceName());
        if (dto.getDescription() != null) serviceType.setDescription(dto.getDescription());
        if (dto.getLicensed() != null) serviceType.setLicensed(dto.getLicensed());
        if (dto.getOilChange() != null) serviceType.setOilChangeST(dto.getOilChange());
    }
}
