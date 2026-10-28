package de.wps.ddd.kino.einlasskontrolle.domain;

public interface FilmvorstellungenRepository
{
    void fuegeVorstellungHinzu(Filmvorstellung filmvorstellung);
    Filmvorstellung holeVorstellung(VorstellungsId vorstellungsId);
}
