package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.mappers;

import de.wps.ddd.kino.einlasskontrolle.domain.Kinokarte;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenId;
import de.wps.ddd.kino.einlasskontrolle.domain.Reihe;
import de.wps.ddd.kino.einlasskontrolle.domain.Saal;
import de.wps.ddd.kino.einlasskontrolle.domain.Sitzplatz;
import de.wps.ddd.kino.einlasskontrolle.domain.VorstellungsId;
import de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.model.FilmvorstellungEntity;
import de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.model.KinokarteEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;

@Component
public class KinokarteEntityMapper {

    public KinokarteEntity toEntity(Kinokarte kinokarte) {
        var filmvorstellung = new FilmvorstellungEntity();
        filmvorstellung.setVorstellungsId(kinokarte.getFilmvorstellung().id());

        Instant entwertungsZeitpunkt = kinokarte.getEntwertungszeitpunkt() == null
                ? null
                : kinokarte.getEntwertungszeitpunkt().wert().atZone(ZoneId.systemDefault()).toInstant();

        return new KinokarteEntity(
                kinokarte.getId().id(),
                filmvorstellung,
                kinokarte.getSaal().wert(),
                kinokarte.getReihe().wert(),
                kinokarte.getSitzplatz().wert(),
                entwertungsZeitpunkt
        );
    }

    public Kinokarte toDomain(KinokarteEntity entity) {
        var saal = Arrays.stream(Saal.values())
                .filter(value -> value.wert() == entity.getSaal())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unbekannter Saal: " + entity.getSaal()));

        var kinokarte = new Kinokarte(
                new KinokartenId(entity.getKinokartenId()),
                new Reihe(entity.getReihe()),
                new Sitzplatz(entity.getSitzplatz()),
                saal,
                new VorstellungsId(entity.getFilmvorstellung().getVorstellungsId()));

        if (entity.getEntwertungszeitpunkt() != null) {
            kinokarte.entwerten();
        }
        return kinokarte;
    }
}
