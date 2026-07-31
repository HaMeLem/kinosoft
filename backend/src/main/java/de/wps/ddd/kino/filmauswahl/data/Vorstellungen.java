package de.wps.ddd.kino.filmauswahl.data;

import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface Vorstellungen extends Repository<Vorstellung, Long> {

    Vorstellung save(Vorstellung vorstellung);

    Optional<Vorstellung> findByUuid(UUID uuid);
}
