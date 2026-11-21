package de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung;

import org.jmolecules.ddd.annotation.ValueObject;
import java.time.LocalDateTime;

/*
 * Der Zeitpunkt, zu dem die Filmvorführung beginnen soll.
 */

@ValueObject
public record GeplanterBeginn(LocalDateTime zeitpunkt) {
}