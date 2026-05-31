package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.DocumentResponseDTO;
import br.com.edu.fatec.ipemControl.dto.UserDocumentResponseDTO;
import br.com.edu.fatec.ipemControl.entity.DepartureLog;
import br.com.edu.fatec.ipemControl.entity.Document;
import br.com.edu.fatec.ipemControl.entity.User;
import br.com.edu.fatec.ipemControl.entity.UserDocument;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.DepartureLogRepository;
import br.com.edu.fatec.ipemControl.repository.DocumentRepository;
import br.com.edu.fatec.ipemControl.repository.UserDocumentRepository;
import br.com.edu.fatec.ipemControl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserDocumentRepository userDocumentRepository;
    private final DepartureLogRepository departureLogRepository;
    private final UserRepository userRepository;

    // ── POST /documents ───────────────────────────────────────────
    public DocumentResponseDTO create(Integer departureLogId, String fileName, String filePath) {
        DepartureLog departureLog = departureLogRepository.findById(departureLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Saída não encontrada."));

        Document document = Document.builder()
                .departureLog(departureLog)
                .fileName(fileName)
                .filePath(filePath)
                .build();

        return toDTO(documentRepository.save(document));
    }

    // ── GET /documents?departureLogId={id} ────────────────────────
    public List<DocumentResponseDTO> findByDepartureLog(Integer departureLogId) {
        return documentRepository.findByDepartureLogId(departureLogId)
                .stream().map(this::toDTO).toList();
    }

    // ── GET /documents/{id} ───────────────────────────────────────
    public DocumentResponseDTO findById(Integer id) {
        return documentRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado."));
    }

    // ── POST /documents/{id}/assign/{registration} ────────────────
    public UserDocumentResponseDTO assignToUser(Integer documentId, Integer registration) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado."));

        User user = userRepository.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        UserDocument userDocument = UserDocument.builder()
                .document(document)
                .user(user)
                .accessedAt(LocalDateTime.now())
                .downloaded(false)
                .read(false)
                .build();

        return toUserDocDTO(userDocumentRepository.save(userDocument));
    }

    // ── PATCH /documents/user-doc/{id}/read ──────────────────────
    public UserDocumentResponseDTO markAsRead(Integer userDocumentId) {
        UserDocument userDocument = userDocumentRepository.findById(userDocumentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vínculo de documento não encontrado."));
        userDocument.setRead(true);
        return toUserDocDTO(userDocumentRepository.save(userDocument));
    }

    // ── PATCH /documents/user-doc/{id}/download ───────────────────
    public UserDocumentResponseDTO markAsDownloaded(Integer userDocumentId) {
        UserDocument userDocument = userDocumentRepository.findById(userDocumentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vínculo de documento não encontrado."));
        userDocument.setDownloaded(true);
        userDocument.setRead(true);
        return toUserDocDTO(userDocumentRepository.save(userDocument));
    }

    // ── GET /documents/user/{registration}/stats ──────────────────
    public UserDocumentResponseDTO getUserStats(Integer registration) {
        UserDocumentResponseDTO dto = new UserDocumentResponseDTO();
        dto.setReceived(userDocumentRepository.countByUserRegistration(registration));
        dto.setReadCount(userDocumentRepository.countByUserRegistrationAndReadTrue(registration));
        dto.setDownloadedCount(userDocumentRepository.countByUserRegistrationAndDownloadedTrue(registration));
        return dto;
    }

    // ── Mapeamento ────────────────────────────────────────────────
    private DocumentResponseDTO toDTO(Document d) {
        DocumentResponseDTO dto = new DocumentResponseDTO();
        dto.setId(d.getId());
        dto.setFileName(d.getFileName());
        dto.setFilePath(d.getFilePath());
        if (d.getDepartureLog() != null)
            dto.setDepartureLogId(d.getDepartureLog().getId());
        dto.setCreatedAt(d.getCreatedAt());
        return dto;
    }

    private UserDocumentResponseDTO toUserDocDTO(UserDocument ud) {
        UserDocumentResponseDTO dto = new UserDocumentResponseDTO();
        dto.setId(ud.getId());
        dto.setIsRead(ud.isRead());
        dto.setDownloaded(ud.isDownloaded());
        dto.setAccessedAt(ud.getAccessedAt());
        if (ud.getUser() != null)
            dto.setUserRegistration(ud.getUser().getRegistration());
        if (ud.getDocument() != null)
            dto.setDocumentId(ud.getDocument().getId());
        return dto;
    }
}