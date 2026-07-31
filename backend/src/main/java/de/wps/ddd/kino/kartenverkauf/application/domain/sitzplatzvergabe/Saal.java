package de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

/**
 * Ein Saal des Kartenverkaufs als Aggregat, identifiziert über den {@link Saalname}. Trägt die
 * Bestuhlung (Reihen × Plätze pro Reihe), aus der ein leerer {@link Saalplan} aufgebaut wird.
 */
@AggregateRoot
public record Saal(@Identity Saalname name, int reihen, int plaetzeProReihe) {
}
