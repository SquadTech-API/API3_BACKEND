package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.FuelingDTO;
import br.com.edu.fatec.IPEMControl.DTO.FuelingHistoryDTO;
import br.com.edu.fatec.IPEMControl.DTO.SavedFuelingDTO;
import br.com.edu.fatec.IPEMControl.Service.FuelingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/fueling")
public class FuelingController {

    private final FuelingService fuelingService;

    public FuelingController(FuelingService fuelingService) {
        this.fuelingService = fuelingService;
    }

    @PostMapping
    public ResponseEntity<SavedFuelingDTO> createFueling(@RequestBody FuelingDTO dto) {
        SavedFuelingDTO savedFueling = fuelingService.save(dto);
        return ResponseEntity
                .created(URI.create("/fueling/" + savedFueling.getFuelingId()))
                .body(savedFueling);
    }

    @GetMapping("/history")
    public ResponseEntity<List<FuelingHistoryDTO>> getFuelingHistory(
            @RequestParam(required = false) Integer vehicleId) {

        List<FuelingHistoryDTO> history = fuelingService.findHistory(vehicleId);
        return ResponseEntity.ok(history);
    }
}
