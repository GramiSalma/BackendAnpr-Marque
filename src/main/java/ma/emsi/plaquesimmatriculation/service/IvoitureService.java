package ma.emsi.plaquesimmatriculation.service;

import ma.emsi.plaquesimmatriculation.entities.Status;
import ma.emsi.plaquesimmatriculation.entities.voiture;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


import org.springframework.web.multipart.MultipartFile;
@Service
public interface IvoitureService {
    voiture addVoiture(voiture voiture);
    voiture updateVoiture(voiture voiture);
    void deleteVoiture(int id);


    List<voiture> findVoitureByPrefecture(String prefecture);
    List<voiture> findVoitureByAgence(String prefecture);
    voiture findVoitureByPlaque(String prefecture);
    List<voiture> getVoitures();
    List<voiture> searchVoitures(String query);
   voiture findVoitureById(int id);

    // Nouvelle méthode pour enregistrer l'image et détecter la plaque
    String saveImage(MultipartFile file) throws IOException;
    String detectPlate(String filePath);
    voiture findVoitureByImagePath(String imagePath);

}
