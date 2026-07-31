package de.wps.ddd.kino.filmauswahl.data;

import org.springframework.data.repository.Repository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface Saele extends Repository<Saal, Long> {

    Optional<Saal> findByName(String name);

    Saal save(Saal saal);
}
