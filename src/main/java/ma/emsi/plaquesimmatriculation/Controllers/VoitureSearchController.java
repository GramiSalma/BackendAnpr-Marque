package ma.emsi.plaquesimmatriculation.Controllers;

import lombok.extern.slf4j.Slf4j;
import ma.emsi.plaquesimmatriculation.entities.Status;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import ma.emsi.plaquesimmatriculation.entities.voiture;
import ma.emsi.plaquesimmatriculation.service.IvoitureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@CrossOrigin("*")
@Slf4j
@RestController
@RequestMapping("/voitures")
@RequiredArgsConstructor
public class VoitureSearchController {

    private final IvoitureService ivoitureService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<voiture>> getAllVoitures() {
        return new ResponseEntity<>(ivoitureService.getVoitures(), HttpStatus.OK);
    }

    @GetMapping("/historique")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<voiture> getHistorique() {
        return ivoitureService.getVoitures();
    }

    @GetMapping("/historique/prefecture/{Prefecture}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<voiture> getVoituresByPrefecture(@PathVariable String Prefecture) {
        return ivoitureService.findVoitureByPrefecture(Prefecture);
    }

    @GetMapping("/historique/plaque/{Plaque}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public voiture getVoituresByPlaque(@PathVariable String Plaque) {
        return ivoitureService.findVoitureByPlaque(Plaque);
    }

    @GetMapping("/historique/agence/{Agence}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<voiture> getVoituresByAgence(@PathVariable String Agence) {
        return ivoitureService.findVoitureByAgence(Agence);
    }

    @GetMapping("/findOne/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public voiture findByID(@PathVariable int id) {
        return ivoitureService.findVoitureById(id);
    }

    @GetMapping("/images/{filename:.+}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get("C:/Users/salma/OneDrive/Bureau/ang-proj/frontend-immatriculation/src/assets/images_voitures/").resolve(filename).normalize();
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.IMAGE_JPEG)  // Définir le type MIME des images
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not serve the file. Error: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<voiture>> searchVoitures(@RequestParam String query) {
        List<voiture> voitures = ivoitureService.searchVoitures(query);
        return new ResponseEntity<>(voitures, HttpStatus.OK);
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<voiture> addVoiture(@RequestBody voiture voiture) {
        voiture newVoiture = ivoitureService.addVoiture(voiture);
        return new ResponseEntity<>(newVoiture, HttpStatus.CREATED);
    }

    @PostMapping("/uploadAndDetect")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<voiture> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Chemin où l'image sera sauvegardée
            String folderPath = "C:/Users/salma/OneDrive/Bureau/ang-proj/frontend-immatriculation/src/assets/images_voitures/";
            File directory = new File(folderPath);
            if (!directory.exists()) {
                directory.mkdirs(); // Crée le répertoire s'il n'existe pas
            }

            // Générer un nom de fichier unique pour éviter les conflits
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(folderPath + fileName);
            Files.write(path, file.getBytes());

            String detectedPlate = ivoitureService.detectPlate(path.toString());

            voiture existingVoiture = ivoitureService.findVoitureByPlaque(detectedPlate);

            if (existingVoiture != null) {
                existingVoiture.setImage(fileName); // Enregistrer uniquement le nom de fichier
                return new ResponseEntity<>(ivoitureService.updateVoiture(existingVoiture), HttpStatus.OK);
            } else {
                voiture newVoiture = new voiture();
                newVoiture.setPlaque(detectedPlate);
                newVoiture.setImage(fileName); // Enregistrer uniquement le nom de fichier
                return new ResponseEntity<>(ivoitureService.addVoiture(newVoiture), HttpStatus.CREATED);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<voiture> updateVoiture(@PathVariable int id, @RequestBody voiture voiture) {
        voiture.setIdVoiture(id);
        voiture updatedVoiture = ivoitureService.updateVoiture(voiture);
        return new ResponseEntity<>(updatedVoiture, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteVoiture(@PathVariable int id) {
        ivoitureService.deleteVoiture(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
