package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.NotificationResponseDTO;
import br.com.edu.fatec.ipemControl.entity.Notification;
import br.com.edu.fatec.ipemControl.entity.User;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.NotificationRepository;
import br.com.edu.fatec.ipemControl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // ── GET /notifications — notificações do usuário logado ───────
    public List<NotificationResponseDTO> findByUser(Integer registration) {
        return notificationRepository
                .findByUserRegistrationOrderByCreatedAtDesc(registration)
                .stream().map(this::toDTO).toList();
    }

    // ── GET /notifications/unread-count — badge do sino ──────────
    public long countUnread(Integer registration) {
        return notificationRepository.countByUserRegistrationAndReadFalse(registration);
    }

    // ── PATCH /notifications/{id}/read (#U03) ────────────────────
    public NotificationResponseDTO markAsRead(Integer id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada."));
        notification.setRead(true);
        return toDTO(notificationRepository.save(notification));
    }

    // ── PATCH /notifications/read-all (#U03) ─────────────────────
    public void markAllAsRead(Integer registration) {
        notificationRepository.markAllAsRead(registration);
    }

    // ── Criação interna (chamada pelos services) ──────────────────
    public void create(Integer userRegistration, String type, String title,
                       String message, String reason, Integer referenceId) {
        User user = userRepository.findByRegistration(userRegistration)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .reason(reason)
                .referenceId(referenceId)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    // ── Mapeamento ────────────────────────────────────────────────
    private NotificationResponseDTO toDTO(Notification n) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setReason(n.getReason());
        dto.setReferenceId(n.getReferenceId());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}