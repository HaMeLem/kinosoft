package de.wps.ddd.kino.filmvorfuehrung.domain.filmvorstellung;

import org.jmolecules.ddd.annotation.ValueObject;
import java.time.LocalDateTime;

/*
 * Zeitpunkt, zu dem die Filmvorführung tatsächlich beginnt.
 * Der tatsächliche Beginn einer Filmvorführung kann sich vom geplanten Beginn einer Filmvorführung unterscheiden. 
 */

@ValueObject
public record TatsaechlicherBeginn(LocalDateTime zeitpunkt) {
}