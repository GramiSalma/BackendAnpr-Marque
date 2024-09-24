package ma.emsi.plaquesimmatriculation.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class voiture {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int idVoiture;
    private String plaque ;
    private String prefecture;
    private String serie;
    private String numOrdre;
    private String image ;
    @Enumerated(EnumType.STRING) // Optionnel: Pour stocker l'énumération sous forme de chaîne de caractères dans la base de données
    private Status status = Status.NOT_DETECTED;
    @ManyToOne
    private agence agence;
}





