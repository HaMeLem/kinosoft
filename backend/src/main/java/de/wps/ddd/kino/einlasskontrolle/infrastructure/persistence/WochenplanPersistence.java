package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence;

import de.wps.ddd.kino.einlasskontrolle.domain.Filmvorstellung;
import de.wps.ddd.kino.einlasskontrolle.domain.VorstellungsId;
import de.wps.ddd.kino.einlasskontrolle.domain.Wochenplan;
import org.springframework.stereotype.Repository;

/**
 * Implementiert die Schnittstelle Wochenplan aus der Domain.
 * Übernimmt Speicherung und Abrufen von Filmvorstellungen.
 * Die konkrete Datenbankanbindung ist noch nicht umgesetzt.
 */
@Repository
public class WochenplanPersistence implements Wochenplan {

    @Override
    public void fuegeVorstellungHinzu(Filmvorstellung filmvorstellung) {
        // TODO: Filmvorstellung in der Datenbank speichern.
        throw new UnsupportedOperationException("Persistenz noch nicht implementiert");
    }

    @Override
    public Filmvorstellung holeVorstellung(VorstellungsId vorstellungsId) {
        // TODO: Filmvorstellung anhand ihrer ID aus der Datenbank laden.
        throw new UnsupportedOperationException("Persistenz noch nicht implementiert");
    }
}
