package de.wps.ddd.kino.filmvorfuehrung.domain.filmvorstellung;

import org.jmolecules.ddd.annotation.ValueObject;
import java.time.LocalDateTime;

/*
 * Der Zeitpunkt, an dem die Filmvorführung endet, wenn sie zum geplanten Zeitpunkt beginnt.
 */

@ValueObject
public record GeplantesEnde(LocalDateTime zeitpunkt) {
}
