package br.com.edu.fatec.ipemControl.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/uploads")
public class UploadController {

    private static final String UPLOAD_DIR = "uploads/NF/";

    @PostMapping
    public ResponseEntity<String> upload(@RequestParam("foto") MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path destination = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("/" + UPLOAD_DIR + fileName);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erro ao save arquivo.");
        }
    }
}