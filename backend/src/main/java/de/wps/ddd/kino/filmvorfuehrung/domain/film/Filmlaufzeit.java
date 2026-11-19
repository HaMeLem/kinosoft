package de.wps.ddd.kino.filmvorfuehrung.domain.film;

import org.jmolecules.ddd.annotation.ValueObject;
import java.time.Duration;

/*
 * Laufzeit des Films in Sekunden
 */

@ValueObject
public record Filmlaufzeit(Duration laufzeit) {
}