package de.wps.ddd.kino.filmvorfuehrung.dtos;

import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.UnterbrechungId;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsende;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.VorfuehrungId;

public record UnterbrechungBeendenRequestDto (
    VorfuehrungId vorfuehrungId,
    UnterbrechungId unterbrechungId,
    Unterbrechungsende unterbrechungsende
){}
