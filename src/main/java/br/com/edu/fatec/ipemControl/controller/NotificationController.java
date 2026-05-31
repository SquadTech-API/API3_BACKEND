package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.NotificationResponseDTO;
import br.com.edu.fatec.ipemControl.security.UserPrincipal;
import br.com.edu.fatec.ipemControl.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // GET /notifications — notificações do usuário logado
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> findAll(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                notificationService.findByUser(principal.getUser().getRegistration()));
    }

    // GET /notifications/unread-count — badge do sino
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> countUnread(
            @AuthenticationPrincipal UserPrincipal principal) {
        long count = notificationService.countUnread(
                principal.getUser().getRegistration());
        return ResponseEntity.ok(Map.of("count", count));
    }

    // PATCH /notifications/{id}/read (#U03)
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(@PathVariable Integer id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    // PATCH /notifications/read-all (#U03)
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllAsRead(principal.getUser().getRegistration());
        return ResponseEntity.ok().build();
    }
}