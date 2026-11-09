package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.mappers;

import de.wps.ddd.kino.einlasskontrolle.domain.*;
import de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.model.FilmvorstellungEntity;
import de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.model.KinokarteEntity;
import de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.repositories.FilmvorstellungJpaRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

@Component("einlasskontrolle_KinokarteEntityMapper")
public class KinokarteEntityMapper {

    private final FilmvorstellungJpaRepository filmvorstellungRepository;

    public KinokarteEntityMapper(
            FilmvorstellungJpaRepository filmvorstellungRepository) {
        this.filmvorstellungRepository = filmvorstellungRepository;
    }

    public KinokarteEntity toEntity(Kinokarte kinokarte) {
        var vorstellungsId = kinokarte.getFilmvorstellung().id();

        // VorstellungsId kann null sein, wenn die Kinokarte gespeichert wurde, bevor die Vorstellung hinzugefügt wurde.
        // In diesem Fall wird die Kinokarte nur mit der VorstellungsId gespeichert, die später zugeordnet werden kann.
        var filmvorstellung = filmvorstellungRepository
                .findById(vorstellungsId)
                .orElse(null);

        var entwertungszeitpunkt = kinokarte.getEntwertungszeitpunkt() == null
                ? null
                : kinokarte.getEntwertungszeitpunkt().wert();

        return new KinokarteEntity(
                kinokarte.getId().id(),
                vorstellungsId,
                filmvorstellung,
                kinokarte.getSaal().wert(),
                kinokarte.getReihe().wert(),
                kinokarte.getSitzplatz().wert(),
                entwertungszeitpunkt
        );
    }

    public Kinokarte toDomain(KinokarteEntity entity) {
        var saal = Arrays.stream(Saal.values())
                .filter(value -> value.wert() == entity.getSaal())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unbekannter Saal: " + entity.getSaal()));

        var entwertungszeitpunkt = entity.getEntwertungszeitpunkt() == null
                ? null
                : new Entwertungszeitpunkt(entity.getEntwertungszeitpunkt());

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
