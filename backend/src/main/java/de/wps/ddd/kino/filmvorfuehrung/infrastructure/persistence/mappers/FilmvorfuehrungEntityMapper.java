package de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.mappers;

import de.wps.ddd.kino.filmvorfuehrung.domain.film.FilmId;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechung;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.UnterbrechungId;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsbeginn;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsende;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.Filmvorfuehrung;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.GeplanterBeginn;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.Saal;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.TatsaechlicherBeginn;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.VorfuehrungId;
import de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.model.FilmEntity;
import de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.model.FilmvorfuehrungEntity;
import de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.model.UnterbrechungEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class FilmvorfuehrungEntityMapper {

    public FilmvorfuehrungEntity toEntity(
            Filmvorfuehrung filmvorfuehrung,
            FilmEntity film) {

        return new FilmvorfuehrungEntity(
                filmvorfuehrung.getId().uuid(),
                zeitpunkt(filmvorfuehrung.getGeplanterBeginn()),
                zeitpunkt(filmvorfuehrung.getTatsaechlicherBeginn()),
                filmvorfuehrung.getSaal().wert(),
                filmvorfuehrung.getStatus(),
                filmvorfuehrung.getWerbelaufzeit().minuten(),
                film,
                filmvorfuehrung.getUnterbrechungen().stream()
                        .map(this::toEntity)
                        .toList());
    }

    public Filmvorfuehrung toDomain(FilmvorfuehrungEntity entity) {
        var tatsaechlicherBeginn = entity.getTatsaechlicherBeginn() == null
                ? null
                : new TatsaechlicherBeginn(entity.getTatsaechlicherBeginn());

        return new Filmvorfuehrung(
                new VorfuehrungId(entity.getVorfuehrungId()),
                new GeplanterBeginn(entity.getGeplanterBeginn()),
                tatsaechlicherBeginn,
                saal(entity.getSaal()),
                entity.getStatus(),
                new FilmId(entity.getFilm().getFilmId()),
                entity.getUnterbrechungen().stream()
                        .map(this::toDomain)
                        .toList());
    }

    private UnterbrechungEntity toEntity(Unterbrechung unterbrechung) {
        return new UnterbrechungEntity(
                unterbrechung.getUnterbrechungId().uuid(),
                unterbrechung.getUnterbrechungsbeginn().zeitpunkt(),
                unterbrechung.getUnterbrechungsende() == null
                        ? null
                        : unterbrechung.getUnterbrechungsende().zeitpunkt());
    }

    private Unterbrechung toDomain(UnterbrechungEntity entity) {
        var unterbrechungsende = entity.getUnterbrechungsende() == null
                ? null
                : new Unterbrechungsende(entity.getUnterbrechungsende());

        return new Unterbrechung(
                new UnterbrechungId(entity.getUnterbrechungId()),
                new Unterbrechungsbeginn(entity.getUnterbrechungsbeginn()),
                unterbrechungsende);
    }

    private Saal saal(int saalWert) {
        return Arrays.stream(Saal.values())
                .filter(saal -> saal.wert() == saalWert)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unbekannter Saalwert: " + saalWert));
    }

    private LocalDateTime zeitpunkt(GeplanterBeginn beginn) {
        return beginn == null ? null : beginn.zeitpunkt();
    }

    private LocalDateTime zeitpunkt(TatsaechlicherBeginn beginn) {
        return beginn == null ? null : beginn.zeitpunkt();
    }
}
