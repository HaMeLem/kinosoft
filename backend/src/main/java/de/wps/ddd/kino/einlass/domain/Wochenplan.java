package de.wps.ddd.kino.einlass.domain;

public interface Wochenplan
{
    void fuegeVorstellungHinzu(Filmvorstellung filmvorstellung);
    Filmvorstellung holeVorstellung(VorstellungsId vorstellungsId);
}
