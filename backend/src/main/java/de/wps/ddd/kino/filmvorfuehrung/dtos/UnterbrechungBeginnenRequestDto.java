package de.wps.ddd.kino.filmvorfuehrung.dtos;

import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsbeginn;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.VorfuehrungId;

public record UnterbrechungBeginnenRequestDto (
    VorfuehrungId vorfuehrungId,
    Unterbrechungsbeginn unterbrechungsbeginn
){}
