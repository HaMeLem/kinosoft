package de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung;

import org.jmolecules.ddd.annotation.ValueObject;
import java.time.LocalDateTime;

/*
 * Der Zeitpunkt, an dem eine Filmvorführung unterbrochen wird.
 */

@ValueObject
public record Unterbrechungsbeginn(LocalDateTime zeitpunkt) {
}