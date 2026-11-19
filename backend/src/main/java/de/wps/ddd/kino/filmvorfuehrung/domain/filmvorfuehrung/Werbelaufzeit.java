package de.wps.ddd.kino.filmvorfuehrung.domain.filmvorstellung;

import org.jmolecules.ddd.annotation.ValueObject;
import java.time.Duration;

/*
 * Laufzeit der Werbung vor dem Film in Sekunden
 */

@ValueObject
public record Werbelaufzeit(Duration laufzeit) {
}