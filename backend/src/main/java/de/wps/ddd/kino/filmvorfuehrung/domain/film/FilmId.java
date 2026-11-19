package de.wps.ddd.kino.filmvorfuehrung.domain.film;

import java.util.UUID;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record FilmId(UUID uuid) {
}