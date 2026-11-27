package de.wps.ddd.kino.filmvorfuehrung.dtos;

import java.util.Map;

import de.wps.ddd.kino.filmvorfuehrung.domain.film.Filmlaufzeit;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.Filmtitel;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechung;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.UnterbrechungId;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.GeplanterBeginn;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.Saal;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.Status;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.TatsaechlicherBeginn;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.VorfuehrungId;

public record FilmvorfuehrungResponseDto(
    VorfuehrungId id,
    GeplanterBeginn geplanterBeginn,
    TatsaechlicherBeginn tatsaechlicherBeginn,
    Saal saal,
    Status status,
    Filmtitel filmtitel,
    Filmlaufzeit filmlaufzeit,
    Map<UnterbrechungId,Unterbrechung> unterbrechungen
){}