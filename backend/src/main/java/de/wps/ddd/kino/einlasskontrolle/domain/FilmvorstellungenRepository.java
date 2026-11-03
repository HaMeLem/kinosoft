package de.wps.ddd.kino.einlasskontrolle.domain;

import java.util.Optional;

public interface FilmvorstellungenRepository
{
    void fuegeVorstellungHinzu(Filmvorstellung filmvorstellung);
    Optional<Filmvorstellung> holeVorstellung(VorstellungsId id);

}
