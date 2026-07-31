package de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.repositories;

import de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.model.KinokarteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KinokarteRepository extends JpaRepository<KinokarteEntity, UUID> {

    List<KinokarteEntity> findByAuftragsnummer(UUID auftragsnummer);
}
