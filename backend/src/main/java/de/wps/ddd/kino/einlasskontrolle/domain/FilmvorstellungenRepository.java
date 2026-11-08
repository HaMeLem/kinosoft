package de.wps.ddd.kino.einlasskontrolle.domain;

import java.util.Optional;

import de.wps.ddd.kino.common.error.RessourceNichtGefunden;

public interface FilmvorstellungenRepository
{
    void fuegeVorstellungHinzu(Filmvorstellung filmvorstellung);
    Optional<Filmvorstellung> hole(VorstellungsId id);

    // brauche ich, um die Vorstellung zu laden und bei Nichtvorhandensein eine Exception zu werfen
    // um die Methode RessourceNichtGefunden.wenn(...) zu nutzen, die eine Exception wirft, 
    // wenn die Vorstellung nicht gefunden wird
    default Filmvorstellung holeOderFehler(VorstellungsId id)
    {
        Optional<Filmvorstellung> gefunden = hole(id);

        RessourceNichtGefunden.wenn(
                gefunden.isEmpty(),
                "Filmvorstellung mit ID " + id + " nicht gefunden"
        );

        return gefunden.get();
    }

}
