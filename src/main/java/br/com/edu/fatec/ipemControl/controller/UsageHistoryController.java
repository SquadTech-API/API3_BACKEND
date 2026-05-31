package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.UsageHistoryCardDTO;
import br.com.edu.fatec.ipemControl.service.UsageHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usage-history")
@RequiredArgsConstructor
public class UsageHistoryController {

    private final UsageHistoryService usageHistoryService;

    // GET /usage-history/vehicle/{id}
    @GetMapping("/vehicle/{id}")
    public ResponseEntity<List<UsageHistoryCardDTO>> findByVehicle(@PathVariable Integer id) {
        return ResponseEntity.ok(usageHistoryService.getUsageHistoryByVehicle(id));
    }
}