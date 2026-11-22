package de.wps.ddd.kino.filmvorfuehrung.domain.film;

import org.jmolecules.ddd.annotation.ValueObject;

/*
 * Laufzeit des Films in Minuten
 */

@ValueObject
public record Filmlaufzeit(long minuten) {
}