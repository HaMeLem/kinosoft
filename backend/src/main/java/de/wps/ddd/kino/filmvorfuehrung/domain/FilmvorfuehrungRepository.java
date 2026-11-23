package de.wps.ddd.kino.filmvorfuehrung.domain;



import java.util.Optional;

public interface FilmvorfuehrungRepository {
    void fuegeVorfuehrungHinzu(Filmvorfuehrung filmvorfuehrung);
    //Optional<Filmvorstellung> hole(VorstellungsId id);
}
