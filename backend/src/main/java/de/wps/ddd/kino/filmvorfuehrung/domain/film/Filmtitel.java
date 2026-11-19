package de.wps.ddd.kino.filmvorfuehrung.domain.film;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Filmtitel(String titel) {
}