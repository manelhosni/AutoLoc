package tn.esprit.autoloc.autolocapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.autolocapi.domain.Contrat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IContratRepository extends JpaRepository<Contrat, Long> {
    Optional<Contrat> findFirstByDateSignature(LocalDate dateSignature);
    List<Contrat> findByDateSignature(LocalDate dateSignature);
}
