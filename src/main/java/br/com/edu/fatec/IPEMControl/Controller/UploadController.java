package br.com.edu.fatec.IPEMControl.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/uploads")
public class UploadController {

    private static final String PASTA = "uploads/NF/";

    @PostMapping
    public ResponseEntity<String> upload(@RequestParam("foto") MultipartFile arquivo) {
        try {
            Files.createDirectories(Paths.get(PASTA));

            String nomeArquivo = System.currentTimeMillis() + "_" + arquivo.getOriginalFilename();
            Path destino = Paths.get(PASTA + nomeArquivo);
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("/" + PASTA + nomeArquivo);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erro ao salvar arquivo.");
        }
    }
}