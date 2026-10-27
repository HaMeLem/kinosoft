package de.wps.ddd.kino.einlasskontrolle.domain;

public interface Wochenplan
{
    void fuegeVorstellungHinzu(Filmvorstellung filmvorstellung);
    Filmvorstellung holeVorstellung(VorstellungsId vorstellungsId);
}
