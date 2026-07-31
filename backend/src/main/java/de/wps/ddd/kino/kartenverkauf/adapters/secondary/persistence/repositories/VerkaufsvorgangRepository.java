package de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.repositories;

import de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.model.VerkaufsvorgangEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface VerkaufsvorgangRepository extends JpaRepository<VerkaufsvorgangEntity, UUID> {

    @Query("select v from VerkaufsvorgangEntity v where v.zahlungsvorgang.zahlungsvorgangId = :zahlungsvorgangId")
    Optional<VerkaufsvorgangEntity> findByZahlungsvorgang(@Param("zahlungsvorgangId") UUID zahlungsvorgangId);
}
