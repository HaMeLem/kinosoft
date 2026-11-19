package de.wps.ddd.kino.filmvorfuehrung.domain.filmvorstellung;

import org.jmolecules.ddd.annotation.ValueObject;

/*
 * Das Kino hat genau zwei Säle.
 */

@ValueObject
public enum Saal {
    KLEINER_SAAL(1),
    GROSSER_SAAL(2);

    private final int wert;

    Saal(int wert) {
        this.wert = wert;
    }

    public int wert() {
        return wert;
    }
}