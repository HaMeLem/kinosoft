package de.wps.ddd.kino.filmvorfuehrung.domain;

import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.Filmvorfuehrung;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.VorfuehrungId;

import java.util.Optional;

public interface FilmvorfuehrungRepository {
    void fuegeVorfuehrungHinzu(Filmvorfuehrung filmvorfuehrung);
    Optional<Filmvorfuehrung> hole(VorfuehrungId id);
}
