package ma.emsi.plaquesimmatriculation.repositories;
import ma.emsi.plaquesimmatriculation.entities.voiture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface voitureRepository extends JpaRepository<voiture,Integer>{
    voiture findByIdVoiture(int id );
    List<voiture> findByAgence_NomAgence(String nomAgence);
    List<voiture> findByPrefecture(String Prefecture);
    voiture findByPlaque(String Plaque);
    voiture findByImage(String image);
    List<voiture> findByPlaqueContainingOrPrefectureContainingOrAgence_NomAgenceContaining(String plaque, String prefecture, String nomAgence);

}
