package de.wps.ddd.kino.einlasskontrolle.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum EinlassVorlauf
{
    EINLASS_VORLAUF_MINUTEN(30);

    private final int wert;

    EinlassVorlauf(int wert) {this.wert = wert;}

    public int wert()
    {
        return wert;
    }
}
