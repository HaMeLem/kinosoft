package de.wps.ddd.kino.filmvorfuehrung.domain.film;

import org.jmolecules.ddd.annotation.ValueObject;

/*
 * Laufzeit des Films in Sekunden
 */

@ValueObject
public record Filmlaufzeit(long sekunden) {
}