package de.wps.ddd.kino.einlass.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum Saal
{
    SAAL_1(1),
    SAAL_2(2);

    private final int wert;

    Saal(int wert)
    {
        this.wert = wert;
    }

    public int wert()
    {
        return wert;
    }
}
