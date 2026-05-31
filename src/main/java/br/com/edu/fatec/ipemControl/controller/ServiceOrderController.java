package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.ServiceOrderDTO;
import br.com.edu.fatec.ipemControl.dto.ServiceOrderResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-order")
@CrossOrigin(origins = "*")
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    public ServiceOrderController(ServiceOrderService serviceOrderService) {
        this.serviceOrderService = serviceOrderService;
    }

    @PostMapping
    public ResponseEntity<ServiceOrderResponseDTO> create(@RequestBody ServiceOrderDTO dto) {
        ServiceOrderResponseDTO response = serviceOrderService.create(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ServiceOrderResponseDTO>> list() {
        return ResponseEntity.ok(serviceOrderService.findAll());
    }
}
