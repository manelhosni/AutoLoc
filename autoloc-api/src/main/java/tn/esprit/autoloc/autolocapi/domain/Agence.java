package tn.esprit.autoloc.autolocapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;

    private String nom;

    private String ville;

    private String adresse;

    private String telephone;


    @OneToMany(mappedBy = "agence", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Employe> employes;

    @OneToMany(mappedBy = "agence", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Vehicule> vehicules;
}
