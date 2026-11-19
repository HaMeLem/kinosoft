package de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung;

import org.jmolecules.ddd.annotation.ValueObject;
import java.time.LocalDateTime;

/*
 * Der Zeitpunkt, an dem die Unterbrechung einer Filmvorführung beendet ist.
 */

@ValueObject
public record Unterbrechungsende(LocalDateTime zeitpunkt) {
}