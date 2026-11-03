package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence;

import de.wps.ddd.kino.einlasskontrolle.domain.Filmvorstellung;
import de.wps.ddd.kino.einlasskontrolle.domain.VorstellungsId;
import de.wps.ddd.kino.einlasskontrolle.domain.FilmvorstellungenRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class FilmvorstellungenRepositoryImpl implements FilmvorstellungenRepository
{

    @Override
    public void fuegeVorstellungHinzu(Filmvorstellung filmvorstellung) {
        // TODO: Filmvorstellung in der Datenbank speichern.
        throw new UnsupportedOperationException("Persistenz noch nicht implementiert");
    }

    @Override
    public Optional<Filmvorstellung> holeVorstellung(VorstellungsId vorstellungsId) {
        // TODO: Filmvorstellung anhand ihrer ID aus der Datenbank laden.
        throw new UnsupportedOperationException("Persistenz noch nicht implementiert");
    }
}
