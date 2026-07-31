package de.wps.ddd.kino.wochenplanung.persistence;

import de.wps.ddd.kino.wochenplanung.filmkatalog.Film;
import de.wps.ddd.kino.wochenplanung.filmkatalog.Filmkatalog;
import de.wps.ddd.kino.wochenplanung.filmkatalog.Filmtitel;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FilmRepository implements Filmkatalog {

    private final ConcurrentHashMap<Filmtitel, Film> filme = new ConcurrentHashMap<>();

    @Override
    public Optional<Film> finde(Filmtitel titel) {
        return Optional.ofNullable(filme.get(titel));
    }

    @Override
    public void speichere(Film film) {
        filme.put(film.titel(), film);
    }
}
