package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.ServiceOrderDTO;
import br.com.edu.fatec.IPEMControl.DTO.ServiceOrderResponseDTO;
import br.com.edu.fatec.IPEMControl.Service.ServiceOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-order")
@CrossOrigin(origins = "*")
public class ServiceOrderController {

    @Autowired
    private ServiceOrder serviceOrder;

    @PostMapping
    public ResponseEntity<ServiceOrderResponseDTO> criar(@RequestBody ServiceOrderDTO dto) {
        ServiceOrderResponseDTO resposta = serviceOrder.criar(dto);
        return ResponseEntity.status(201).body(resposta);
    }

    @GetMapping
    public ResponseEntity<List<ServiceOrderResponseDTO>> listar() {
        return ResponseEntity.ok(serviceOrder.listarTodas());
    }
}