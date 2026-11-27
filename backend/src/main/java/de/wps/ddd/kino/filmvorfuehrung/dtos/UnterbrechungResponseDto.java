package de.wps.ddd.kino.filmvorfuehrung.dtos;

import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.UnterbrechungId;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.VorfuehrungId;

public record UnterbrechungResponseDto (
    VorfuehrungId vorfuehrungId,
    UnterbrechungId unterbrechungId
){}

