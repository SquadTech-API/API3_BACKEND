package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.FuelingDTO;
import br.com.edu.fatec.ipemControl.dto.FuelingHistoryDTO;
import br.com.edu.fatec.ipemControl.dto.SavedFuelingDTO;
import br.com.edu.fatec.ipemControl.service.FuelingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/fueling")
@RequiredArgsConstructor
public class FuelingController {

    private final FuelingService fuelingService;

    // POST /fueling
    @PostMapping
    public ResponseEntity<SavedFuelingDTO> create(@RequestBody FuelingDTO dto) {
        SavedFuelingDTO saved = fuelingService.save(dto);
        return ResponseEntity
                .created(URI.create("/fueling/" + saved.getId()))
                .body(saved);
    }

    // GET /fueling/history?vehicleId={id}
    @GetMapping("/history")
    public ResponseEntity<List<FuelingHistoryDTO>> history(
            @RequestParam(required = false) Integer vehicleId) {
        return ResponseEntity.ok(fuelingService.findHistory(vehicleId));
    }
}