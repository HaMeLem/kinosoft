package de.wps.ddd.kino.filmvorfuehrung.domain.film;

import java.util.UUID;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record FilmId(UUID uuid) {
    //Verwendung für die Filmobjekte die über das WochenplanDTO ohne ID kommen
    public static FilmId neu()
    {
        return new FilmId(UUID.randomUUID());
    }
}