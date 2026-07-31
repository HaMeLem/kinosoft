package de.wps.ddd.kino.filmauswahl.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.jmolecules.ddd.annotation.Repository
@org.springframework.stereotype.Repository
public interface AktuelleFilme extends Repository<Film, Long> {
    @Query("select f from Film f join fetch f.vorstellungen v where v.beginn >= :start and v.beginn < :ende")
    List<Film> findeVorstellungenZwischen(LocalDateTime start, LocalDateTime ende);

    Optional<Film> findByTitel(String titel);

    Film save(Film film);
}
