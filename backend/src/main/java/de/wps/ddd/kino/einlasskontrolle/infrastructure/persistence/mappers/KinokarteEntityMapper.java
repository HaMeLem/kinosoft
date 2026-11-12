package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.mappers;

import de.wps.ddd.kino.einlasskontrolle.domain.*;
import de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.model.KinokarteEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component("einlasskontrolle_KinokarteEntityMapper")
public class KinokarteEntityMapper {

    public KinokarteEntity toEntity(Kinokarte kinokarte) {

        var entwertungszeitpunkt = kinokarte.getEntwertungszeitpunkt() == null
                ? null
                : kinokarte.getEntwertungszeitpunkt().wert();

        return new KinokarteEntity(
                kinokarte.getId().id(),
                kinokarte.getFilmvorstellung().id(),
                kinokarte.getSaal().wert(),
                kinokarte.getReihe().wert(),
                kinokarte.getSitzplatz().wert(),
                entwertungszeitpunkt
        );
    }

    public Kinokarte toDomain(KinokarteEntity entity) {
        var entwertungszeitpunkt = entity.getEntwertungszeitpunkt() == null
                ? null
                : new Entwertungszeitpunkt(entity.getEntwertungszeitpunkt());

        var saal = Arrays.stream(Saal.values())
                .filter(möglicherSaal -> möglicherSaal.wert() == entity.getSaal())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unbekannter Saalwert: " + entity.getSaal()));

        // Im regulären Konstruktur wird der entwertungszeitpunkt per default auf null gesetzt.
        // Über die Methode "rekonstruiere" kann der entwertungszeitpunkt gesetzt werden, der in der Datenbank persistiert ist.
        return Kinokarte.rekonstruiere(
                new KinokartenId(entity.getKinokartenId()),
                new Reihe(entity.getReihe()),
                new Sitzplatz(entity.getSitzplatz()),
                saal,
                new VorstellungsId(entity.getVorstellungsId()),
                entwertungszeitpunkt
        );
    }
}
