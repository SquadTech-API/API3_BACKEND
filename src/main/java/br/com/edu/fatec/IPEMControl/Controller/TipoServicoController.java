package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.Entities.TipoServico;
import br.com.edu.fatec.IPEMControl.Repository.TipoServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipo-servicos")
public class TipoServicoController {

    @Autowired
    private TipoServicoRepository tipoServicoRepository;

    @GetMapping
    public ResponseEntity<List<TipoServico>> listar() {
        return ResponseEntity.ok(tipoServicoRepository.findAll());
    }
}