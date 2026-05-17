package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.UsageHistoryCardDTO;
import br.com.edu.fatec.IPEMControl.Service.UsageHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usage-history")
@CrossOrigin("*")
public class UsageHistoryController {

    private final UsageHistoryService usageHistoryService;

    public UsageHistoryController(UsageHistoryService usageHistoryService) {
        this.usageHistoryService = usageHistoryService;
    }

    @GetMapping("/vehicle/{id}")
    public ResponseEntity<List<UsageHistoryCardDTO>> getUsageHistoryByVehicle(@PathVariable Integer id) {
        return ResponseEntity.ok(usageHistoryService.getUsageHistoryByVehicle(id));
    }
}