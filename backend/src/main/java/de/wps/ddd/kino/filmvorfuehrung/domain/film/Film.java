package de.wps.ddd.kino.filmvorfuehrung.domain.film;

import lombok.Getter;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@Entity 
@Getter
public class Film {
    @Identity private final FilmId filmId;
    private final Filmlaufzeit filmlaufzeit;
    private final Filmtitel filmtitel;

    public Film(
        FilmId filmId,
        Filmlaufzeit filmlaufzeit,
        Filmtitel filmtitel) {
            this.filmId = filmId;
            this.filmlaufzeit = filmlaufzeit;
            this.filmtitel = filmtitel;
    }
}
