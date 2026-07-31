package de.wps.ddd.kino.wochenplanung.filmkatalog;

import org.jmolecules.ddd.annotation.Repository;

import java.util.Optional;

@Repository
public interface Filmkatalog {

    Optional<Film> finde(Filmtitel titel);

    void speichere(Film film);
}
