package de.wps.ddd.kino.wochenplanung.filmkatalog;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum Altersfreigabe {
    FSK0(0),
    FSK6(6),
    FSK12(12),
    FSK16(16),
    FSK18(18);

    private final int wert;

    Altersfreigabe(int wert) {
        this.wert = wert;
    }

    public int wert() {
        return wert;
    }
}
