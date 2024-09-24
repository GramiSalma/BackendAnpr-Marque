package ma.emsi.plaquesimmatriculation.Controllers;

import ma.emsi.plaquesimmatriculation.entities.Status;
import ma.emsi.plaquesimmatriculation.entities.voiture;
import ma.emsi.plaquesimmatriculation.repositories.voitureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ReconniasanceController {

    private final WebClient webClient;
    private final voitureRepository voitureRepository;

    @Autowired
    public ReconniasanceController(WebClient.Builder webClientBuilder, voitureRepository voitureRepository) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:5000").build();
        this.voitureRepository = voitureRepository;
    }

    @PostMapping(value = "/detect", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public Mono<Map<String, Object>> detect(@RequestBody Map<String, String> request) {
        String filePath = request.get("filePath").replace("\\", "/");
        return this.webClient.post()
                .uri("/detect")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(Map.of("filePath", filePath)))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String uploadDir = System.getProperty("user.dir") + "/uploads";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String uniqueID = UUID.randomUUID().toString();
        String filePath = Paths.get(uploadDir, uniqueID + "_" + file.getOriginalFilename()).toString();
        File convFile = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return Map.of("filePath", filePath.replace("\\", "/"));
    }

    @PostMapping("/save-detection")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, String>> saveDetectionResult(@RequestBody DetectionData detectionData) {
        try {
            // Lire l'image depuis le chemin fourni
            Path sourcePath = Paths.get(detectionData.getFilePath());
            File sourceFile = sourcePath.toFile();
            if (!sourceFile.exists()) {
                // Retourner un message JSON en cas d'erreur
                Map<String, String> errorResponse = Map.of("message", "Image not found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Enregistrer l'image dans un nouveau dossier
            String newDir = "C:/Users/salma/OneDrive/Bureau/ang-proj/frontend-immatriculation/src/assets/images_voitures";
            // String newDir = "/app/images_voitures";
            File dir = new File(newDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String newFilePath = newDir + "/" + sourceFile.getName();
            Files.copy(sourcePath, Paths.get(newFilePath));

            // Créer une nouvelle instance de voiture
            voiture newVoiture = new voiture();
            newVoiture.setImage(newFilePath); // Enregistrer le nouveau chemin de l'image
            newVoiture.setPlaque(detectionData.getPlate()); // Enregistrer la plaque détectée
            newVoiture.setStatus(detectionData.isDetected() ? Status.AVAILABLE : Status.NOT_DETECTED); // Définir le statut en fonction du booléen 'detected'
            newVoiture.setNumOrdre(detectionData.getOrderNumber()); // Numéro d'ordre
            newVoiture.setSerie(detectionData.getSerialNumber()); // Numéro de série
            newVoiture.setPrefecture(detectionData.getPrefecture()); // Numéro de série
            // Sauvegarder l'entité voiture dans la base de données
            voitureRepository.save(newVoiture);

            // Retourner un message de succès en JSON
            Map<String, String> response = Map.of("message", "Detection result saved successfully");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            // Retourner un message d'erreur en JSON
            Map<String, String> errorResponse = Map.of("message", "Error saving detection result");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    // Classe interne pour recevoir les données de détection du frontend
    static class DetectionData {
        private String filePath;
        private String plate;
        private boolean detected;
        private String orderNumber;       // Ajouter le numéro d'ordre
        private String serialNumber;      // Ajouter le numéro de série
        private String prefecture;

        // Getters et setters
        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getPlate() {
            return plate;
        }

        public void setPlate(String plate) {
            this.plate = plate;
        }

        public boolean isDetected() {
            return detected;
        }

        public void setDetected(boolean detected) {
            this.detected = detected;
        }
        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getPrefecture() {
            return prefecture;
        }

        public void setPrefecture(String prefecture) {
            this.prefecture = prefecture;
        }
    }
}