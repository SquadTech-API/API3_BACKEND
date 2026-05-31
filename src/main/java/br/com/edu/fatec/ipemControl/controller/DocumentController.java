package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.DocumentResponseDTO;
import br.com.edu.fatec.ipemControl.dto.UserDocumentResponseDTO;
import br.com.edu.fatec.ipemControl.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    // POST /documents — vincula documento a uma saída
    @PostMapping
    public ResponseEntity<DocumentResponseDTO> create(@RequestBody Map<String, Object> body) {
        Integer departureLogId = (Integer) body.get("departureLogId");
        String fileName        = (String) body.get("fileName");
        String filePath        = (String) body.get("filePath");
        return ResponseEntity.status(201).body(
                documentService.create(departureLogId, fileName, filePath));
    }

    // GET /documents?departureLogId={id}
    @GetMapping
    public ResponseEntity<List<DocumentResponseDTO>> findByDepartureLog(
            @RequestParam Integer departureLogId) {
        return ResponseEntity.ok(documentService.findByDepartureLog(departureLogId));
    }

    // GET /documents/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(documentService.findById(id));
    }

    // POST /documents/{id}/assign/{registration} — vincula ao usuário
    @PostMapping("/{id}/assign/{registration}")
    public ResponseEntity<UserDocumentResponseDTO> assignToUser(
            @PathVariable Integer id,
            @PathVariable Integer registration) {
        return ResponseEntity.status(201).body(
                documentService.assignToUser(id, registration));
    }

    // PATCH /documents/user-doc/{userDocId}/read
    @PatchMapping("/user-doc/{userDocId}/read")
    public ResponseEntity<UserDocumentResponseDTO> markAsRead(
            @PathVariable Integer userDocId) {
        return ResponseEntity.ok(documentService.markAsRead(userDocId));
    }

    // PATCH /documents/user-doc/{userDocId}/download
    @PatchMapping("/user-doc/{userDocId}/download")
    public ResponseEntity<UserDocumentResponseDTO> markAsDownloaded(
            @PathVariable Integer userDocId) {
        return ResponseEntity.ok(documentService.markAsDownloaded(userDocId));
    }

    // GET /documents/user/{registration}/stats
    @GetMapping("/user/{registration}/stats")
    public ResponseEntity<UserDocumentResponseDTO> getUserStats(
            @PathVariable Integer registration) {
        return ResponseEntity.ok(documentService.getUserStats(registration));
    }
}