package de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung;

import org.jmolecules.ddd.annotation.ValueObject;

/*
 * Laufzeit der Werbung vor dem Film in Minuten
 */

@ValueObject
public record Werbelaufzeit(long minuten) {
}