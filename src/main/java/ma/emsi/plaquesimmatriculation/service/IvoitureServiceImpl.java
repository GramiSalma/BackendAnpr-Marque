package ma.emsi.plaquesimmatriculation.service;

import ma.emsi.plaquesimmatriculation.entities.Status;
import ma.emsi.plaquesimmatriculation.entities.voiture;
import ma.emsi.plaquesimmatriculation.repositories.voitureRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class IvoitureServiceImpl implements IvoitureService {
    private final voitureRepository voitureRepository;

    public IvoitureServiceImpl(voitureRepository voitureRepository) {
        this.voitureRepository = voitureRepository;
    }

    @Override
    public List<voiture> searchVoitures(String query) {
        return voitureRepository.findByPlaqueContainingOrPrefectureContainingOrAgence_NomAgenceContaining(query, query, query);
    }

    @Override
    public voiture findVoitureById(int id) {
        return voitureRepository.findByIdVoiture(id);
    }

    @Override
    public voiture addVoiture(voiture voiture) {
        return voitureRepository.save(voiture);
    }

    @Override
    public voiture updateVoiture(voiture voiture) {
        return voitureRepository.findById(voiture.getIdVoiture()).map(existingVoiture -> {
            existingVoiture.setImage(voiture.getImage());
            existingVoiture.setPlaque(voiture.getPlaque());
            existingVoiture.setSerie(voiture.getSerie());
            existingVoiture.setNumOrdre(voiture.getNumOrdre());
            existingVoiture.setAgence(voiture.getAgence());
            existingVoiture.setPrefecture(voiture.getPrefecture());
            existingVoiture.setStatus(voiture.getStatus());
            return voitureRepository.save(existingVoiture);
        }).orElseThrow(() -> new IllegalArgumentException("Cette voiture n'existe pas"));
    }

    @Override
    public List<voiture> findVoitureByPrefecture(String prefecture) {
        return voitureRepository.findByPrefecture(prefecture);
    }

    @Override
    public List<voiture> findVoitureByAgence(String agence) {
        return voitureRepository.findByAgence_NomAgence(agence);
    }

    @Override
    public void deleteVoiture(int id) {
        if (!voitureRepository.existsById(id)) {
            throw new IllegalArgumentException("Aucune voiture trouvée pour suppression");
        }
        voitureRepository.deleteById(id);
    }

    @Override
    public voiture findVoitureByPlaque(String plaque) {
        return voitureRepository.findByPlaque(plaque);
    }

    @Override
    public List<voiture> getVoitures() {
        return voitureRepository.findAll();
    }

    @Override
    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }
        String folderPath = "C:/car_images/";
        File directory = new File(folderPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String filePath = folderPath + file.getOriginalFilename();
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());
        return filePath;
    }

    @Override
    public String detectPlate(String filePath) {
        // Implémentez la logique de détection des plaques ici
        // Utilisez une bibliothèque ou une API pour la détection
        return null;
    }

    @Override
    public voiture findVoitureByImagePath(String imagePath) {
        return voitureRepository.findByImage(imagePath);
    }
}
