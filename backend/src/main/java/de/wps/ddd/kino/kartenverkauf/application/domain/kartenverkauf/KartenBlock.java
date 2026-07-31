package de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf;

import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Vorstellung;
import org.jmolecules.ddd.annotation.Factory;
import org.springframework.stereotype.Component;

import java.util.List;

@Factory
@Component
public class KartenBlock {
    public List<Kinokarte> erstelleKarten(
            Auftragsnummer auftragsnummer,
            Vorstellung vorstellung,
            ZusammenhaengendePlaetze gewaehltePlaetze) {
        return gewaehltePlaetze.plaetze().stream().map(platzId -> new Kinokarte(
                KinokarteId.neu(),
                auftragsnummer,
                vorstellung.getId(),
                vorstellung.getFilm(),
                vorstellung.getBeginn(),
                vorstellung.getSaal(),
                platzId.reihe(),
                platzId.platz()
        )).toList();
    }
}
