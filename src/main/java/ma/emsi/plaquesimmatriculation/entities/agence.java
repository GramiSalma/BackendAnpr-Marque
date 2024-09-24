package ma.emsi.plaquesimmatriculation.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor


public class agence {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int idAgence;
    @Temporal(TemporalType.DATE)
    private LocalDate dateSouscription;
    private String nomAgence;
    private String adresseAgence;
    private String telephoneAgence;
    @OneToMany(mappedBy = "agence")
    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    private Collection<voiture> voitures;
    }










